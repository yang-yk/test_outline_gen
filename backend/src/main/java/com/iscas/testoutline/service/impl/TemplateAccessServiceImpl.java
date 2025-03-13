package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.entity.TemplateAccess;
import com.iscas.testoutline.entity.enums.TemplatePermission;
import com.iscas.testoutline.repository.TemplateAccessRepository;
import com.iscas.testoutline.service.TemplateAccessService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TemplateAccessServiceImpl implements TemplateAccessService {

    private final TemplateAccessRepository templateAccessRepository;

    @Override
    @Transactional
    public TemplateAccess grantAccess(Long templateId, Long userId, Set<TemplatePermission> permissions, Long grantedBy, LocalDateTime expiresAt) {
        TemplateAccess access = templateAccessRepository.findByTemplateIdAndUserId(templateId, userId)
                .orElse(new TemplateAccess());
        
        access.setTemplateId(templateId);
        access.setUserId(userId);
        access.setPermissions(permissions);
        access.setGrantedBy(grantedBy);
        access.setExpiresAt(expiresAt);
        
        return templateAccessRepository.save(access);
    }

    @Override
    @Transactional
    public void revokeAccess(Long templateId, Long userId) {
        templateAccessRepository.deleteByTemplateIdAndUserId(templateId, userId);
    }

    @Override
    @Transactional
    public TemplateAccess updateAccess(Long templateId, Long userId, Set<TemplatePermission> permissions) {
        TemplateAccess access = templateAccessRepository.findByTemplateIdAndUserId(templateId, userId)
                .orElseThrow(() -> new EntityNotFoundException("未找到访问权限记录"));
        
        access.setPermissions(permissions);
        return templateAccessRepository.save(access);
    }

    @Override
    public boolean hasPermission(Long templateId, Long userId, TemplatePermission permission) {
        return templateAccessRepository.findByTemplateIdAndUserIdAndPermission(templateId, userId, permission.name())
                .isPresent();
    }

    @Override
    public Set<TemplatePermission> getUserPermissions(Long templateId, Long userId) {
        return templateAccessRepository.findByTemplateIdAndUserId(templateId, userId)
                .map(TemplateAccess::getPermissions)
                .orElse(Set.of());
    }

    @Override
    public List<TemplateAccess> getTemplateAccesses(Long templateId) {
        return templateAccessRepository.findByTemplateId(templateId);
    }

    @Override
    public List<TemplateAccess> getUserAccesses(Long userId) {
        return templateAccessRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteTemplateAccesses(Long templateId) {
        templateAccessRepository.deleteByTemplateId(templateId);
    }
} 