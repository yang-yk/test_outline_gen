package com.iscas.testoutline.entity;

import com.iscas.testoutline.entity.enums.TemplatePermission;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "template_access")
public class TemplateAccess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "template_user_permissions",
        joinColumns = @JoinColumn(name = "template_access_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<TemplatePermission> permissions;

    @Column(name = "granted_by")
    private Long grantedBy;

    @Column(name = "granted_at")
    @CreationTimestamp
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
} 