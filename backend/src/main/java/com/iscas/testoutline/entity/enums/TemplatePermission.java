package com.iscas.testoutline.entity.enums;

public enum TemplatePermission {
    READ("读取"),
    WRITE("编辑"),
    DELETE("删除"),
    SHARE("分享"),
    MANAGE("管理");

    private final String description;

    TemplatePermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 