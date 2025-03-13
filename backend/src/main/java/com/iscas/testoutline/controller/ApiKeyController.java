package com.iscas.testoutline.controller;

import com.iscas.testoutline.entity.ApiKey;
import com.iscas.testoutline.entity.User;
import com.iscas.testoutline.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping
    public ResponseEntity<List<ApiKey>> getApiKeys(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(apiKeyService.getApiKeys(user.getId()));
    }

    @PostMapping
    public ResponseEntity<ApiKey> createApiKey(
            @AuthenticationPrincipal User user,
            @RequestParam String name) {
        return ResponseEntity.ok(apiKeyService.createApiKey(user.getId(), name));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> deleteApiKey(
            @AuthenticationPrincipal User user,
            @PathVariable Long keyId) {
        apiKeyService.deleteApiKey(user.getId(), keyId);
        return ResponseEntity.ok().build();
    }
} 