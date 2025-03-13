package com.iscas.testoutline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "test_outlines")
@Data
@EqualsAndHashCode(callSuper = true)
public class TestOutline extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String knowledgeBase;

    @Column(nullable = false)
    private String template;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer maxTokens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Document requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
} 