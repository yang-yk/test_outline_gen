package com.iscas.testoutline.controller;

import com.iscas.testoutline.dto.DocumentResponseDTO;
import com.iscas.testoutline.dto.GenerateOutlineDTO;
import com.iscas.testoutline.dto.TestOutlineResponseDTO;
import com.iscas.testoutline.service.OutlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/outline")
@RequiredArgsConstructor
@Tag(name = "测试大纲", description = "测试大纲相关接口")
public class OutlineController {

    private final OutlineService outlineService;

    @PostMapping("/upload")
    @Operation(summary = "上传需求规格说明书")
    public ResponseEntity<DocumentResponseDTO> uploadRequirement(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(outlineService.uploadRequirement(authentication.getName(), file));
    }

    @GetMapping("/requirement/list")
    @Operation(summary = "获取需求规格说明书列表")
    public ResponseEntity<List<DocumentResponseDTO>> getRequirementList(Authentication authentication) {
        return ResponseEntity.ok(outlineService.getRequirementList(authentication.getName()));
    }

    @GetMapping("/list")
    @Operation(summary = "获取测试大纲列表")
    public ResponseEntity<List<TestOutlineResponseDTO>> getOutlineList(Authentication authentication) {
        return ResponseEntity.ok(outlineService.getOutlineList(authentication.getName()));
    }

    @PostMapping("/generate")
    @Operation(summary = "生成测试大纲")
    public ResponseEntity<TestOutlineResponseDTO> generateOutline(
            Authentication authentication,
            @Valid @RequestBody GenerateOutlineDTO generateDTO
    ) {
        return ResponseEntity.ok(outlineService.generateOutline(authentication.getName(), generateDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除文档")
    public ResponseEntity<Void> deleteDocument(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam String type
    ) {
        outlineService.deleteDocument(authentication.getName(), id, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载文档")
    public ResponseEntity<ByteArrayResource> downloadDocument(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam String type
    ) {
        byte[] data = outlineService.downloadDocument(authentication.getName(), id, type);
        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document." + (type.equals("REQUIREMENT") ? "pdf" : "docx"));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping("/save/{id}")
    @Operation(summary = "保存测试大纲")
    public ResponseEntity<TestOutlineResponseDTO> saveOutline(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody String content
    ) {
        return ResponseEntity.ok(outlineService.saveOutline(authentication.getName(), id, content));
    }
}