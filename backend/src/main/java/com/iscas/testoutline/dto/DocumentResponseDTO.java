package com.iscas.testoutline.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String documentType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}