package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.GenerationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GenerationHistoryRepository extends JpaRepository<GenerationHistory, Long> {
    List<GenerationHistory> findByUserId(Long userId);
    Page<GenerationHistory> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    List<GenerationHistory> findByStatus(GenerationHistory.GenerationStatus status);
    List<GenerationHistory> findByStatusAndCreateTimeBefore(GenerationHistory.GenerationStatus status, LocalDateTime time);
} 