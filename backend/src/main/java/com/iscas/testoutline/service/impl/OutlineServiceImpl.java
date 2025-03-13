package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.dto.DocumentResponseDTO;
import com.iscas.testoutline.dto.GenerateOutlineDTO;
import com.iscas.testoutline.dto.TestOutlineResponseDTO;
import com.iscas.testoutline.entity.Document;
import com.iscas.testoutline.entity.TestOutline;
import com.iscas.testoutline.entity.User;
import com.iscas.testoutline.repository.DocumentRepository;
import com.iscas.testoutline.repository.TestOutlineRepository;
import com.iscas.testoutline.service.DocumentService;
import com.iscas.testoutline.service.OpenAiService;
import com.iscas.testoutline.service.OutlineService;
import com.iscas.testoutline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutlineServiceImpl implements OutlineService {

    private final DocumentRepository documentRepository;
    private final TestOutlineRepository testOutlineRepository;
    private final UserService userService;
    private final OpenAiService openAiService;
    private final DocumentService documentService;

    @Override
    @Transactional
    public DocumentResponseDTO uploadRequirement(String email, MultipartFile file) {
        try {
            // 创建上传目录
            String uploadDir = "uploads/requirements";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 保存文档信息
            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(filePath.toString());
            document.setFileSize(file.getSize());
            document.setFileType(getFileExtension(file.getOriginalFilename()));
            document.setDocumentType("REQUIREMENT");
            document.setUser(userService.getUserByEmail(email));

            document = documentRepository.save(document);
            return convertToDocumentDTO(document);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public List<DocumentResponseDTO> getRequirementList(String email) {
        User user = userService.getUserByEmail(email);
        return documentRepository.findByUserAndDocumentTypeOrderByCreateTimeDesc(user, "REQUIREMENT")
                .stream()
                .map(this::convertToDocumentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestOutlineResponseDTO> getOutlineList(String email) {
        User user = userService.getUserByEmail(email);
        return testOutlineRepository.findByUserOrderByCreateTimeDesc(user)
                .stream()
                .map(this::convertToTestOutlineDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TestOutlineResponseDTO generateOutline(String email, GenerateOutlineDTO generateDTO) {
        User user = userService.getUserByEmail(email);
        
        // 获取需求文档内容（如果有）
        String requirementContent = "";
        Document requirement = null;
        if (generateDTO.getRequirementId() != null) {
            requirement = documentRepository.findById(generateDTO.getRequirementId())
                    .orElseThrow(() -> new RuntimeException("需求文档不存在"));
            try {
                requirementContent = new String(Files.readAllBytes(Paths.get(requirement.getFilePath())));
            } catch (IOException e) {
                throw new RuntimeException("读取需求文档失败", e);
            }
        }

        // 调用OpenAI生成测试大纲
        String content = openAiService.generateTestOutline(
            requirementContent,
            generateDTO.getTemplate(),
            generateDTO.getKnowledgeBase(),
            generateDTO.getTemperature(),
            generateDTO.getMaxTokens(),
            null // 使用系统默认的API Key
        );

        TestOutline outline = new TestOutline();
        outline.setTitle(generateDTO.getTitle());
        outline.setContent(content);
        outline.setRequirement(requirement); // 可以为null
        outline.setUser(user);
        outline.setModel(generateDTO.getModel());
        outline.setKnowledgeBase(generateDTO.getKnowledgeBase());
        outline.setTemplate(generateDTO.getTemplate());
        outline.setTemperature(generateDTO.getTemperature());
        outline.setMaxTokens(generateDTO.getMaxTokens());

        outline = testOutlineRepository.save(outline);
        return convertToTestOutlineDTO(outline);
    }

    @Override
    @Transactional
    public void deleteDocument(String email, Long id, String type) {
        User user = userService.getUserByEmail(email);
        if ("REQUIREMENT".equals(type)) {
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("文档不存在"));
            if (!document.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("无权删除此文档");
            }
            try {
                Files.deleteIfExists(Paths.get(document.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException("文件删除失败", e);
            }
            documentRepository.delete(document);
        } else if ("OUTLINE".equals(type)) {
            TestOutline outline = testOutlineRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("测试大纲不存在"));
            if (!outline.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("无权删除此测试大纲");
            }
            testOutlineRepository.delete(outline);
        }
    }

    @Override
    public byte[] downloadDocument(String email, Long id, String type) {
        User user = userService.getUserByEmail(email);
        try {
            if ("REQUIREMENT".equals(type)) {
                Document document = documentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("文档不存在"));
                if (!document.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("无权下载此文档");
                }
                return Files.readAllBytes(Paths.get(document.getFilePath()));
            } else if ("OUTLINE".equals(type)) {
                TestOutline outline = testOutlineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("测试大纲不存在"));
                if (!outline.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("无权下载此测试大纲");
                }
                
                // 将测试大纲内容转换为Word文档
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                documentService.writeTestOutline(outline.getContent(), outputStream);
                return outputStream.toByteArray();
            }
            throw new RuntimeException("不支持的文档类型");
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    @Transactional
    public TestOutlineResponseDTO saveOutline(String email, Long id, String content) {
        User user = userService.getUserByEmail(email);
        TestOutline outline = testOutlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("测试大纲不存在"));
        if (!outline.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改此测试大纲");
        }

        outline.setContent(content);
        outline = testOutlineRepository.save(outline);
        return convertToTestOutlineDTO(outline);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private DocumentResponseDTO convertToDocumentDTO(Document document) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setFileSize(document.getFileSize());
        dto.setFileType(document.getFileType());
        dto.setDocumentType(document.getDocumentType());
        dto.setCreateTime(document.getCreateTime());
        dto.setUpdateTime(document.getUpdateTime());
        return dto;
    }

    private TestOutlineResponseDTO convertToTestOutlineDTO(TestOutline outline) {
        TestOutlineResponseDTO dto = new TestOutlineResponseDTO();
        dto.setId(outline.getId());
        dto.setTitle(outline.getTitle());
        dto.setContent(outline.getContent());
        dto.setModel(outline.getModel());
        dto.setKnowledgeBase(outline.getKnowledgeBase());
        dto.setTemplate(outline.getTemplate());
        dto.setTemperature(outline.getTemperature());
        dto.setMaxTokens(outline.getMaxTokens());
        dto.setRequirement(convertToDocumentDTO(outline.getRequirement()));
        dto.setCreateTime(outline.getCreateTime());
        dto.setUpdateTime(outline.getUpdateTime());
        return dto;
    }
} 