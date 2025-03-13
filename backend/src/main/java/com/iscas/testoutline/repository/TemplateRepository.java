package com.iscas.testoutline.repository;

import com.iscas.testoutline.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    Optional<Template> findByIsDefaultTrue();
    
    List<Template> findByCategory(String category);
    
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT t.category FROM Template t WHERE t.category IS NOT NULL")
    List<String> findAllCategories();
    
    List<Template> findByNameContainingOrDescriptionContaining(String name, String description);
} 