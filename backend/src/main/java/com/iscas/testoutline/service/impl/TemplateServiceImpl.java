package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.dto.TemplateDTO;
import com.iscas.testoutline.entity.Template;
import com.iscas.testoutline.repository.TemplateRepository;
import com.iscas.testoutline.service.TemplateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    @Override
    @Transactional
    public Template createTemplate(TemplateDTO templateDTO) {
        if (templateRepository.existsByName(templateDTO.getName())) {
            throw new IllegalArgumentException("模板名称已存在");
        }

        Template template = new Template();
        BeanUtils.copyProperties(templateDTO, template);
        
        if (Boolean.TRUE.equals(templateDTO.getIsDefault())) {
            templateRepository.findByIsDefaultTrue()
                    .ifPresent(defaultTemplate -> {
                        defaultTemplate.setIsDefault(false);
                        templateRepository.save(defaultTemplate);
                    });
        }
        
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public Template updateTemplate(Long id, TemplateDTO templateDTO) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在"));

        if (!template.getName().equals(templateDTO.getName()) 
                && templateRepository.existsByName(templateDTO.getName())) {
            throw new IllegalArgumentException("模板名称已存在");
        }

        BeanUtils.copyProperties(templateDTO, template);
        template.setId(id); // 确保ID不被覆盖
        
        if (Boolean.TRUE.equals(templateDTO.getIsDefault())) {
            templateRepository.findByIsDefaultTrue()
                    .ifPresent(defaultTemplate -> {
                        if (!defaultTemplate.getId().equals(id)) {
                            defaultTemplate.setIsDefault(false);
                            templateRepository.save(defaultTemplate);
                        }
                    });
        }
        
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在"));
                
        if (Boolean.TRUE.equals(template.getIsDefault())) {
            throw new IllegalArgumentException("不能删除默认模板");
        }
        
        templateRepository.deleteById(id);
    }

    @Override
    public Template getTemplate(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在"));
    }

    @Override
    public Template getDefaultTemplate() {
        return templateRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new EntityNotFoundException("未设置默认模板"));
    }

    @Override
    @Transactional
    public Template setDefaultTemplate(Long id) {
        Template newDefault = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在"));
                
        templateRepository.findByIsDefaultTrue()
                .ifPresent(oldDefault -> {
                    oldDefault.setIsDefault(false);
                    templateRepository.save(oldDefault);
                });
                
        newDefault.setIsDefault(true);
        return templateRepository.save(newDefault);
    }

    @Override
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public List<Template> getTemplatesByCategory(String category) {
        return templateRepository.findByCategory(category);
    }

    @Override
    public List<String> getAllCategories() {
        return templateRepository.findAllCategories();
    }

    @Override
    public List<Template> searchTemplates(String keyword) {
        return templateRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }
} 