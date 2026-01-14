package com.pennywise.backend.category.repository;

import com.pennywise.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndParentIsNull(Long userId);

    List<Category> findByParentId(Long parentId);

    Optional<Category> findByUserIdAndName(Long userId, String name);

    boolean existsByUserIdAndName(Long userId, String name);
}