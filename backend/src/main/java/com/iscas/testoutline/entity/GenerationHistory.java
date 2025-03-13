package com.iscas.testoutline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "generation_histories")
@Data
@EqualsAndHashCode(callSuper = true)
public class GenerationHistory extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String requirement;

    @Column(nullable = false)
    private String templateId;

    @Column(nullable = false)
    private String knowledgeBase;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer maxTokens;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStatus status = GenerationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStep currentStep = GenerationStep.PENDING;

    @Column(nullable = false)
    private Integer progress = 0;

    private String resultFilePath;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "estimated_remaining_seconds")
    private Integer estimatedRemainingSeconds;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum GenerationStatus {
        PENDING("等待处理"),
        PROCESSING("处理中"),
        COMPLETED("已完成"),
        FAILED("失败");

        private final String description;

        GenerationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 