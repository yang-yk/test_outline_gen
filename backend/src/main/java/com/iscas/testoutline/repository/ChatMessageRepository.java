package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * 获取用户的聊天历史记录
     */
    Page<ChatMessage> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 删除用户的聊天历史记录
     */
    void deleteByUserId(Long userId);
} 