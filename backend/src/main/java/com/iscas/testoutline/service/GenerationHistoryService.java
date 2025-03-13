package com.iscas.testoutline.service;

import com.iscas.testoutline.entity.GenerationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenerationHistoryService {
    
    /**
     * 创建生成历史记录
     */
    GenerationHistory createHistory(GenerationHistory history);
    
    /**
     * 更新生成历史记录
     */
    GenerationHistory updateHistory(GenerationHistory history);
    
    /**
     * 获取生成历史记录
     */
    GenerationHistory getHistory(Long id);
    
    /**
     * 分页获取用户的生成历史
     */
    Page<GenerationHistory> getUserHistory(Long userId, Pageable pageable);
    
    /**
     * 获取所有待处理的任务
     */
    List<GenerationHistory> getPendingTasks();
    
    /**
     * 获取所有正在处理的任务
     */
    List<GenerationHistory> getProcessingTasks();
    
    /**
     * 清理过期的历史记录
     */
    void cleanupExpiredHistory(int days);
} 