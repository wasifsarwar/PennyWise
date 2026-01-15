package com.pennywise.category.repository;

import com.pennywise.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);

    List<Category> findByUserIdAndParentIsNull(UUID userId);

    List<Category> findByParentId(UUID parentId);

    Optional<Category> findByUserIdAndName(UUID userId, String name);

    boolean existsByUserIdAndName(UUID userId, String name);
}
