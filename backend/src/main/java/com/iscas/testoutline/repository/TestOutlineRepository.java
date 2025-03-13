package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.TestOutline;
import com.iscas.testoutline.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestOutlineRepository extends JpaRepository<TestOutline, Long> {
    List<TestOutline> findByUserOrderByCreateTimeDesc(User user);
} 