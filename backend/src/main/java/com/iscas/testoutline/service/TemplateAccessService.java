package com.iscas.testoutline.service;

import com.iscas.testoutline.entity.TemplateAccess;
import com.iscas.testoutline.entity.enums.TemplatePermission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TemplateAccessService {
    
    /**
     * 授予用户模板权限
     */
    TemplateAccess grantAccess(Long templateId, Long userId, Set<TemplatePermission> permissions, Long grantedBy, LocalDateTime expiresAt);
    
    /**
     * 撤销用户模板权限
     */
    void revokeAccess(Long templateId, Long userId);
    
    /**
     * 更新用户模板权限
     */
    TemplateAccess updateAccess(Long templateId, Long userId, Set<TemplatePermission> permissions);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long templateId, Long userId, TemplatePermission permission);
    
    /**
     * 获取用户的模板权限
     */
    Set<TemplatePermission> getUserPermissions(Long templateId, Long userId);
    
    /**
     * 获取模板的所有访问权限
     */
    List<TemplateAccess> getTemplateAccesses(Long templateId);
    
    /**
     * 获取用户的所有模板访问权限
     */
    List<TemplateAccess> getUserAccesses(Long userId);
    
    /**
     * 删除模板的所有访问权限
     */
    void deleteTemplateAccesses(Long templateId);
} 