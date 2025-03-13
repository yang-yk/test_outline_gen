package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByUserId(Long userId);
    Optional<ApiKey> findByKeyValue(String keyValue);
    boolean existsByKeyValue(String keyValue);
} 