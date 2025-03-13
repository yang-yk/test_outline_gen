package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.entity.GenerationHistory;
import com.iscas.testoutline.repository.GenerationHistoryRepository;
import com.iscas.testoutline.service.GenerationHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerationHistoryServiceImpl implements GenerationHistoryService {

    private final GenerationHistoryRepository generationHistoryRepository;

    @Override
    @Transactional
    public GenerationHistory createHistory(GenerationHistory history) {
        return generationHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public GenerationHistory updateHistory(GenerationHistory history) {
        if (!generationHistoryRepository.existsById(history.getId())) {
            throw new EntityNotFoundException("历史记录不存在");
        }
        return generationHistoryRepository.save(history);
    }

    @Override
    public GenerationHistory getHistory(Long id) {
        return generationHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("历史记录不存在"));
    }

    @Override
    public Page<GenerationHistory> getUserHistory(Long userId, Pageable pageable) {
        return generationHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    public List<GenerationHistory> getPendingTasks() {
        return generationHistoryRepository.findByStatus(GenerationHistory.GenerationStatus.PENDING);
    }

    @Override
    public List<GenerationHistory> getProcessingTasks() {
        return generationHistoryRepository.findByStatus(GenerationHistory.GenerationStatus.PROCESSING);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void cleanupExpiredHistory(int days) {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(days);
        List<GenerationHistory> expiredHistories = generationHistoryRepository
                .findByStatusAndCreateTimeBefore(GenerationHistory.GenerationStatus.COMPLETED, expirationTime);
        generationHistoryRepository.deleteAll(expiredHistories);
    }
} 