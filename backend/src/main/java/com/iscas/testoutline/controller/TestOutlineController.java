package com.iscas.testoutline.controller;

import com.iscas.testoutline.dto.BatchGenerationRequest;
import com.iscas.testoutline.entity.GenerationHistory;
import com.iscas.testoutline.entity.GenerationStep;
import com.iscas.testoutline.service.DocumentService;
import com.iscas.testoutline.service.GenerationHistoryService;
import com.iscas.testoutline.service.OpenAiService;
import com.iscas.testoutline.service.TemplateService;
import com.iscas.testoutline.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Tag(name = "测试大纲", description = "测试大纲生成相关接口")
@RestController
@RequestMapping("/outlines")
@RequiredArgsConstructor
public class TestOutlineController {

    private final OpenAiService openAiService;
    private final DocumentService documentService;
    private final TemplateService templateService;
    private final GenerationHistoryService generationHistoryService;
    private final ProgressService progressService;

    @Operation(summary = "根据需求文本生成测试大纲")
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateOutline(
            @RequestParam String requirement,
            @RequestParam(required = false) String templateId,
            @RequestParam(required = false) String knowledgeBase,
            @RequestParam(required = false, defaultValue = "0.7") Double temperature,
            @RequestParam(required = false, defaultValue = "2000") Integer maxTokens,
            @RequestParam(required = false) String apiKey
    ) {
        // 获取模板内容
        String templateContent = null;
        if (templateId != null) {
            templateContent = templateService.getTemplate(Long.parseLong(templateId)).getContent();
        }

        // 生成测试大纲
        String outline = openAiService.generateTestOutline(
                requirement,
                templateContent,
                knowledgeBase,
                temperature,
                maxTokens,
                apiKey
        );

        // 将大纲写入Word文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        documentService.writeTestOutline(outline, outputStream);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "测试大纲.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @Operation(summary = "根据需求文档生成测试大纲")
    @PostMapping("/generate/file")
    public ResponseEntity<byte[]> generateOutlineFromFile(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String knowledgeBase,
            @RequestParam(required = false, defaultValue = "0.7") Double temperature,
            @RequestParam(required = false, defaultValue = "2000") Integer maxTokens,
            @RequestParam(required = false) String apiKey
    ) throws IOException {
        // 读取文档内容
        String requirement = documentService.readWordDocument(file.getInputStream());

        // 获取模板内容
        String templateContent = null;
        if (templateId != null) {
            templateContent = templateService.getTemplate(templateId).getContent();
        }

        // 生成测试大纲
        String outline = openAiService.generateTestOutline(
                requirement,
                templateContent,
                knowledgeBase,
                temperature,
                maxTokens,
                apiKey
        );

        // 将大纲写入Word文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        documentService.writeTestOutline(outline, outputStream);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "测试大纲.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @Operation(summary = "批量生成测试大纲")
    @PostMapping("/batch")
    public ResponseEntity<List<Long>> batchGenerate(
            @Valid @RequestBody BatchGenerationRequest request) {
        // TODO: 从安全上下文中获取当前用户ID
        Long userId = 1L;
        
        final String templateContent;
        if (request.getTemplateId() != null) {
            templateContent = templateService.getTemplate(Long.parseLong(request.getTemplateId())).getContent();
        } else {
            templateContent = null;
        }

        List<Long> taskIds = new ArrayList<>();
        
        for (final String requirement : request.getRequirements()) {
            // 创建历史记录
            GenerationHistory history = new GenerationHistory();
            history.setUserId(userId);
            history.setRequirement(requirement);
            history.setTemplateId(String.valueOf(request.getTemplateId()));
            history.setKnowledgeBase(request.getKnowledgeBase());
            history.setTemperature(request.getTemperature());
            history.setMaxTokens(request.getMaxTokens());
            history.setStatus(GenerationHistory.GenerationStatus.PENDING);
            final GenerationHistory finalHistory = generationHistoryService.createHistory(history);
            
            final Long taskId = finalHistory.getId();
            taskIds.add(taskId);
            
            // 异步生成测试大纲
            CompletableFuture.runAsync(() -> {
                try {
                    // 初始化
                    progressService.updateProgress(taskId, GenerationStep.PENDING);
                    
                    // 分析需求
                    progressService.updateProgress(taskId, GenerationStep.PROCESSING);
                    
                    // 生成大纲
                    progressService.updateProgress(taskId, GenerationStep.PROCESSING);
                    String outline = openAiService.generateTestOutline(
                            requirement,
                            templateContent,
                            request.getKnowledgeBase(),
                            request.getTemperature(),
                            request.getMaxTokens(),
                            request.getApiKey()
                    );
                    
                    // 格式化文档
                    progressService.updateProgress(taskId, GenerationStep.FORMATTING_DOCUMENT);
                    String fileName = "测试大纲_" + UUID.randomUUID() + ".docx";
                    Path filePath = Paths.get("uploads", fileName);
                    Files.createDirectories(filePath.getParent());
                    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    documentService.writeTestOutline(outline, outputStream);
                    
                    // 保存结果
                    progressService.updateProgress(taskId, GenerationStep.SAVING_RESULT);
                    Files.write(filePath, outputStream.toByteArray());
                    
                    // 完成任务
                    GenerationHistory updatedHistory = generationHistoryService.getHistory(taskId);
                    updatedHistory.setResultFilePath(filePath.toString());
                    generationHistoryService.updateHistory(updatedHistory);
                    progressService.completeTask(taskId);
                    
                } catch (Exception e) {
                    // 更新失败状态
                    progressService.failTask(taskId, e.getMessage());
                }
            });
        }
        
        return ResponseEntity.ok(taskIds);
    }

    @Operation(summary = "下载批量生成的测试大纲")
    @GetMapping("/batch/{taskId}/download")
    public ResponseEntity<byte[]> downloadBatchResult(@PathVariable Long taskId) throws IOException {
        GenerationHistory history = generationHistoryService.getHistory(taskId);
        
        if (history.getStatus() != GenerationHistory.GenerationStatus.COMPLETED) {
            return ResponseEntity.badRequest().build();
        }
        
        byte[] content = Files.readAllBytes(Paths.get(history.getResultFilePath()));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "测试大纲_" + taskId + ".docx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    @Operation(summary = "获取生成任务状态")
    @GetMapping("/batch/{taskId}/status")
    public ResponseEntity<GenerationHistory> getTaskStatus(@PathVariable Long taskId) {
        return ResponseEntity.ok(generationHistoryService.getHistory(taskId));
    }

    @Operation(summary = "获取用户的生成历史")
    @GetMapping("/history")
    public ResponseEntity<Page<GenerationHistory>> getUserHistory(Pageable pageable) {
        // TODO: 从安全上下文中获取当前用户ID
        Long userId = 1L;
        return ResponseEntity.ok(generationHistoryService.getUserHistory(userId, pageable));
    }

    @Operation(summary = "下载多个测试大纲（ZIP格式）")
    @PostMapping("/batch/download")
    public ResponseEntity<byte[]> downloadMultipleOutlines(@RequestParam List<Long> taskIds) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Long taskId : taskIds) {
                GenerationHistory history = generationHistoryService.getHistory(taskId);
                if (history.getStatus() == GenerationHistory.GenerationStatus.COMPLETED) {
                    byte[] content = Files.readAllBytes(Paths.get(history.getResultFilePath()));
                    ZipEntry entry = new ZipEntry("测试大纲_" + taskId + ".docx");
                    zos.putNextEntry(entry);
                    zos.write(content);
                    zos.closeEntry();
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "测试大纲批量下载.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }
} 