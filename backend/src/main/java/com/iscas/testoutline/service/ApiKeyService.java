package com.iscas.testoutline.service;

import com.iscas.testoutline.entity.ApiKey;

import java.util.List;

public interface ApiKeyService {
    /**
     * 获取用户的API密钥列表
     */
    List<ApiKey> getApiKeys(Long userId);

    /**
     * 创建新的API密钥
     */
    ApiKey createApiKey(Long userId, String name);

    /**
     * 删除API密钥
     */
    void deleteApiKey(Long userId, Long keyId);

    /**
     * 验证API密钥
     */
    boolean validateApiKey(String keyValue);
} 