package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.entity.GenerationHistory;
import com.iscas.testoutline.entity.GenerationStep;
import com.iscas.testoutline.service.GenerationHistoryService;
import com.iscas.testoutline.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final GenerationHistoryService generationHistoryService;

    @Override
    @Transactional
    public void updateProgress(Long taskId, GenerationStep step) {
        GenerationHistory history = generationHistoryService.getHistory(taskId);
        
        // 更新进度信息
        history.setStatus(GenerationHistory.GenerationStatus.PROCESSING);
        history.setCurrentStep(step);
        
        // 估算剩余时间（基于每个步骤的平均时间）
        int estimatedSecondsPerStep = 30; // 假设每个步骤平均需要30秒
        int remainingSteps = GenerationStep.values().length - step.ordinal() - 1;
        history.setEstimatedRemainingSeconds(remainingSteps * estimatedSecondsPerStep);
        
        generationHistoryService.updateHistory(history);
    }

    @Override
    @Transactional
    public void updateProgress(Long taskId, String step, int progress, Integer estimatedRemainingSeconds) {
        GenerationHistory history = generationHistoryService.getHistory(taskId);
        
        history.setStatus(GenerationHistory.GenerationStatus.PROCESSING);
        history.setProgress(progress);
        history.setCurrentStep(GenerationStep.valueOf(step));
        history.setEstimatedRemainingSeconds(estimatedRemainingSeconds);
        
        generationHistoryService.updateHistory(history);
    }

    @Override
    @Transactional
    public void completeTask(Long taskId) {
        GenerationHistory history = generationHistoryService.getHistory(taskId);
        
        history.setStatus(GenerationHistory.GenerationStatus.COMPLETED);
        history.setProgress(100);
        history.setCurrentStep(GenerationStep.COMPLETED);
        history.setEstimatedRemainingSeconds(0);
        history.setCompletedAt(LocalDateTime.now());
        
        generationHistoryService.updateHistory(history);
    }

    @Override
    @Transactional
    public void failTask(Long taskId, String errorMessage) {
        GenerationHistory history = generationHistoryService.getHistory(taskId);
        
        history.setStatus(GenerationHistory.GenerationStatus.FAILED);
        history.setErrorMessage(errorMessage);
        history.setCompletedAt(LocalDateTime.now());
        
        generationHistoryService.updateHistory(history);
    }
} 