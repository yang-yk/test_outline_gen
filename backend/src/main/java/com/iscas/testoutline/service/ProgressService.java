package com.iscas.testoutline.service;

import com.iscas.testoutline.entity.GenerationHistory;
import com.iscas.testoutline.entity.GenerationStep;

public interface ProgressService {
    
    /**
     * 更新生成进度
     */
    void updateProgress(Long taskId, GenerationStep step);
    
    /**
     * 更新生成进度（自定义进度）
     */
    void updateProgress(Long taskId, String step, int progress, Integer estimatedRemainingSeconds);
    
    /**
     * 完成生成任务
     */
    void completeTask(Long taskId);
    
    /**
     * 标记任务失败
     */
    void failTask(Long taskId, String errorMessage);
} 