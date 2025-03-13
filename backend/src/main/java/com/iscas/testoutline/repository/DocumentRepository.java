package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.Document;
import com.iscas.testoutline.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserAndDocumentTypeOrderByCreateTimeDesc(User user, String documentType);
} 