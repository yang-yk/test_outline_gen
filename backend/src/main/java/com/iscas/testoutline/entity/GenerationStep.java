package com.iscas.testoutline.entity;

public enum GenerationStep {
    PENDING("等待处理"),
    PROCESSING("处理中"),
    FORMATTING_DOCUMENT("格式化文档"),
    SAVING_RESULT("保存结果"),
    COMPLETED("已完成"),
    FAILED("失败");

    private final String description;

    GenerationStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 