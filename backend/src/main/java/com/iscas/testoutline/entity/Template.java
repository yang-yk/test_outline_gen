package com.iscas.testoutline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "templates")
@EqualsAndHashCode(callSuper = true)
public class Template extends BaseEntity {
    
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "category")
    private String category;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessLevel accessLevel = AccessLevel.PRIVATE;

    public enum AccessLevel {
        PUBLIC("公开"),
        PRIVATE("私有");

        private final String description;

        AccessLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 