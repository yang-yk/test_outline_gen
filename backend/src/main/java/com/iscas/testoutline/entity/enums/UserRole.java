package com.iscas.testoutline.entity.enums;

public enum UserRole {
    ADMIN("管理员"),
    TEMPLATE_MANAGER("模板管理员"),
    NORMAL_USER("普通用户");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 