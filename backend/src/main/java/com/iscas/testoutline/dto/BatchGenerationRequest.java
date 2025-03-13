package com.iscas.testoutline.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BatchGenerationRequest {
    @NotNull(message = "模板ID不能为空")
    private String templateId;

    @NotEmpty(message = "需求列表不能为空")
    private List<String> requirements;

    private String knowledgeBase;

    private Double temperature;

    private Integer maxTokens;

    private String apiKey;
} 