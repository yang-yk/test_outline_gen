package com.iscas.testoutline.controller;

import com.iscas.testoutline.service.DocumentService;
import com.iscas.testoutline.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag(name = "文档管理", description = "文档上传下载相关接口")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final OpenAiService openAiService;

    @Operation(summary = "上传Word文档并生成测试大纲")
    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String template,
            @RequestParam(required = false) String knowledgeBase,
            @RequestParam(required = false, defaultValue = "0.7") Double temperature,
            @RequestParam(required = false, defaultValue = "2000") Integer maxTokens,
            @RequestParam(required = false) String apiKey
    ) throws IOException {
        // 读取上传的文档内容
        String content = documentService.readWordDocument(file.getInputStream());
        
        // 调用OpenAI生成测试大纲
        String outline = openAiService.generateTestOutline(content, template, knowledgeBase, temperature, maxTokens, apiKey);
        
        // 将测试大纲写入新的Word文档
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

    @Operation(summary = "生成测试大纲并下载")
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateOutline(
            @RequestParam String requirement,
            @RequestParam(required = false) String template,
            @RequestParam(required = false) String knowledgeBase,
            @RequestParam(required = false, defaultValue = "0.7") Double temperature,
            @RequestParam(required = false, defaultValue = "2000") Integer maxTokens,
            @RequestParam(required = false) String apiKey
    ) {
        // 调用OpenAI生成测试大纲
        String outline = openAiService.generateTestOutline(requirement, template, knowledgeBase, temperature, maxTokens, apiKey);
        
        // 将测试大纲写入Word文档
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
} 