package com.iscas.testoutline.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestOutlineResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String model;
    private String knowledgeBase;
    private String template;
    private Double temperature;
    private Integer maxTokens;
    private DocumentResponseDTO requirement;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 