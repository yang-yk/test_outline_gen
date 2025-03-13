package com.iscas.testoutline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateOutlineDTO {
    @NotNull(message = "需求文档ID不能为空")
    private Long requirementId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "模型不能为空")
    private String model;

    @NotBlank(message = "模板不能为空")
    private String template;

    private String knowledgeBase;

    private Double temperature;

    private Integer maxTokens;
} 