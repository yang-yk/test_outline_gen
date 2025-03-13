package com.iscas.testoutline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chat_messages")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends BaseEntity {
    
    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String model;

    public enum MessageRole {
        USER("用户"),
        ASSISTANT("助手"),
        SYSTEM("系统");

        private final String description;

        MessageRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 