package com.iscas.testoutline.service;

import com.iscas.testoutline.dto.TemplateDTO;
import com.iscas.testoutline.entity.Template;

import java.util.List;

public interface TemplateService {
    
    /**
     * 创建模板
     */
    Template createTemplate(TemplateDTO templateDTO);
    
    /**
     * 更新模板
     */
    Template updateTemplate(Long id, TemplateDTO templateDTO);
    
    /**
     * 删除模板
     */
    void deleteTemplate(Long id);
    
    /**
     * 获取模板详情
     */
    Template getTemplate(Long id);
    
    /**
     * 获取默认模板
     */
    Template getDefaultTemplate();
    
    /**
     * 设置默认模板
     */
    Template setDefaultTemplate(Long id);
    
    /**
     * 获取所有模板
     */
    List<Template> getAllTemplates();
    
    /**
     * 按分类获取模板
     */
    List<Template> getTemplatesByCategory(String category);
    
    /**
     * 获取所有分类
     */
    List<String> getAllCategories();
    
    /**
     * 搜索模板
     */
    List<Template> searchTemplates(String keyword);
} 