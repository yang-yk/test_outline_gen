package com.iscas.testoutline.service;

import com.iscas.testoutline.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {
    
    /**
     * 发送消息并获取回复
     */
    ChatMessage sendMessage(Long userId, String content, String model);
    
    /**
     * 发送消息并获取流式回复
     */
    SseEmitter sendMessageStream(Long userId, String content, String model);
    
    /**
     * 获取聊天历史记录
     */
    Page<ChatMessage> getChatHistory(Long userId, Pageable pageable);
    
    /**
     * 清空聊天历史记录
     */
    void clearChatHistory(Long userId);
    
    /**
     * 获取支持的模型列表
     */
    String[] getAvailableModels();
} 