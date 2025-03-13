package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.TemplateAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemplateAccessRepository extends JpaRepository<TemplateAccess, Long> {
    
    Optional<TemplateAccess> findByTemplateIdAndUserId(Long templateId, Long userId);
    
    List<TemplateAccess> findByTemplateId(Long templateId);
    
    List<TemplateAccess> findByUserId(Long userId);
    
    @Query("SELECT ta FROM TemplateAccess ta WHERE ta.templateId = :templateId AND ta.userId = :userId AND :permission MEMBER OF ta.permissions")
    Optional<TemplateAccess> findByTemplateIdAndUserIdAndPermission(
            @Param("templateId") Long templateId,
            @Param("userId") Long userId,
            @Param("permission") String permission);
    
    void deleteByTemplateId(Long templateId);
    
    void deleteByTemplateIdAndUserId(Long templateId, Long userId);
} 