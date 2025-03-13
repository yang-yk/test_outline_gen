package com.iscas.testoutline.controller;

import com.iscas.testoutline.entity.TemplateAccess;
import com.iscas.testoutline.entity.enums.TemplatePermission;
import com.iscas.testoutline.service.TemplateAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Tag(name = "模板权限管理", description = "模板权限管理相关接口")
@RestController
@RequestMapping("/templates/access")
@RequiredArgsConstructor
public class TemplateAccessController {

    private final TemplateAccessService templateAccessService;

    @Operation(summary = "授予用户模板权限")
    @PostMapping("/{templateId}/users/{userId}")
    public ResponseEntity<TemplateAccess> grantAccess(
            @PathVariable Long templateId,
            @PathVariable Long userId,
            @RequestParam Set<TemplatePermission> permissions,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        // TODO: 从安全上下文中获取当前用户ID
        Long currentUserId = 1L;
        return ResponseEntity.ok(templateAccessService.grantAccess(templateId, userId, permissions, currentUserId, expiresAt));
    }

    @Operation(summary = "撤销用户模板权限")
    @DeleteMapping("/{templateId}/users/{userId}")
    public ResponseEntity<Void> revokeAccess(
            @PathVariable Long templateId,
            @PathVariable Long userId) {
        templateAccessService.revokeAccess(templateId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "更新用户模板权限")
    @PutMapping("/{templateId}/users/{userId}")
    public ResponseEntity<TemplateAccess> updateAccess(
            @PathVariable Long templateId,
            @PathVariable Long userId,
            @RequestParam Set<TemplatePermission> permissions) {
        return ResponseEntity.ok(templateAccessService.updateAccess(templateId, userId, permissions));
    }

    @Operation(summary = "检查用户是否有指定权限")
    @GetMapping("/{templateId}/users/{userId}/check")
    public ResponseEntity<Boolean> hasPermission(
            @PathVariable Long templateId,
            @PathVariable Long userId,
            @RequestParam TemplatePermission permission) {
        return ResponseEntity.ok(templateAccessService.hasPermission(templateId, userId, permission));
    }

    @Operation(summary = "获取用户的模板权限")
    @GetMapping("/{templateId}/users/{userId}/permissions")
    public ResponseEntity<Set<TemplatePermission>> getUserPermissions(
            @PathVariable Long templateId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(templateAccessService.getUserPermissions(templateId, userId));
    }

    @Operation(summary = "获取模板的所有访问权限")
    @GetMapping("/{templateId}")
    public ResponseEntity<List<TemplateAccess>> getTemplateAccesses(
            @PathVariable Long templateId) {
        return ResponseEntity.ok(templateAccessService.getTemplateAccesses(templateId));
    }

    @Operation(summary = "获取用户的所有模板访问权限")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<TemplateAccess>> getUserAccesses(
            @PathVariable Long userId) {
        return ResponseEntity.ok(templateAccessService.getUserAccesses(userId));
    }
} 