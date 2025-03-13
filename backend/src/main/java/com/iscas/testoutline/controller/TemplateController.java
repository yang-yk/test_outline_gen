package com.iscas.testoutline.controller;

import com.iscas.testoutline.dto.TemplateDTO;
import com.iscas.testoutline.entity.Template;
import com.iscas.testoutline.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "模板管理", description = "模板管理相关接口")
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @Operation(summary = "创建模板")
    @PostMapping
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody TemplateDTO templateDTO) {
        return ResponseEntity.ok(templateService.createTemplate(templateDTO));
    }

    @Operation(summary = "更新模板")
    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateDTO templateDTO) {
        return ResponseEntity.ok(templateService.updateTemplate(id, templateDTO));
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplate(id));
    }

    @Operation(summary = "获取默认模板")
    @GetMapping("/default")
    public ResponseEntity<Template> getDefaultTemplate() {
        return ResponseEntity.ok(templateService.getDefaultTemplate());
    }

    @Operation(summary = "设置默认模板")
    @PutMapping("/{id}/default")
    public ResponseEntity<Template> setDefaultTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.setDefaultTemplate(id));
    }

    @Operation(summary = "获取所有模板")
    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @Operation(summary = "按分类获取模板")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Template>> getTemplatesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(templateService.getTemplatesByCategory(category));
    }

    @Operation(summary = "获取所有分类")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(templateService.getAllCategories());
    }

    @Operation(summary = "搜索模板")
    @GetMapping("/search")
    public ResponseEntity<List<Template>> searchTemplates(@RequestParam String keyword) {
        return ResponseEntity.ok(templateService.searchTemplates(keyword));
    }
} 