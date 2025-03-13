package com.iscas.testoutline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TemplateDTO {
    
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotBlank(message = "模板内容不能为空")
    private String content;

    private String description;

    private Boolean isDefault;

    private String category;
} 