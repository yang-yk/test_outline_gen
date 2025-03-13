package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.entity.ApiKey;
import com.iscas.testoutline.entity.User;
import com.iscas.testoutline.exception.EntityNotFoundException;
import com.iscas.testoutline.exception.UnauthorizedException;
import com.iscas.testoutline.repository.ApiKeyRepository;
import com.iscas.testoutline.repository.UserRepository;
import com.iscas.testoutline.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ApiKey> getApiKeys(Long userId) {
        return apiKeyRepository.findByUserId(userId);
    }

    @Override
    public ApiKey createApiKey(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        ApiKey apiKey = new ApiKey();
        apiKey.setName(name);
        apiKey.setKeyValue(generateApiKey());
        apiKey.setUser(user);

        return apiKeyRepository.save(apiKey);
    }

    @Override
    public void deleteApiKey(Long userId, Long keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new EntityNotFoundException("API密钥不存在"));

        if (!apiKey.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("无权删除此API密钥");
        }

        apiKeyRepository.delete(apiKey);
    }

    @Override
    public boolean validateApiKey(String keyValue) {
        return apiKeyRepository.findByKeyValue(keyValue).isPresent();
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
} 