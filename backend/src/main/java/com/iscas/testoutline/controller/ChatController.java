package com.iscas.testoutline.controller;

import com.iscas.testoutline.entity.ChatMessage;
import com.iscas.testoutline.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "聊天", description = "聊天相关接口")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(
            Authentication authentication,
            @RequestParam String content,
            @RequestParam(required = false) String model
    ) {
        // TODO: 从认证信息中获取用户ID
        Long userId = 1L;
        return ResponseEntity.ok(chatService.sendMessage(userId, content, model));
    }

    @Operation(summary = "发送消息（流式响应）")
    @PostMapping(path = "/send/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            Authentication authentication,
            @RequestParam String content,
            @RequestParam(required = false) String model
    ) {
        // TODO: 从认证信息中获取用户ID
        Long userId = 1L;
        return chatService.sendMessageStream(userId, content, model);
    }

    @Operation(summary = "获取聊天历史")
    @GetMapping("/history")
    public ResponseEntity<Page<ChatMessage>> getChatHistory(
            Authentication authentication,
            Pageable pageable
    ) {
        // TODO: 从认证信息中获取用户ID
        Long userId = 1L;
        return ResponseEntity.ok(chatService.getChatHistory(userId, pageable));
    }

    @Operation(summary = "清空聊天历史")
    @DeleteMapping("/history")
    public ResponseEntity<Void> clearChatHistory(Authentication authentication) {
        // TODO: 从认证信息中获取用户ID
        Long userId = 1L;
        chatService.clearChatHistory(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "获取支持的模型列表")
    @GetMapping("/models")
    public ResponseEntity<String[]> getAvailableModels() {
        return ResponseEntity.ok(chatService.getAvailableModels());
    }
} 