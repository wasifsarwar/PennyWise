package com.pennywise.category.service;

import com.pennywise.category.dto.CategoryRequest;
import com.pennywise.category.dto.CategoryResponse;
import com.pennywise.category.entity.Category;
import com.pennywise.category.repository.CategoryRepository;
import com.pennywise.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(UUID userId, CategoryRequest request) {
        // 1. Check if name already exists for this user
        if (categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        // 2. Convert DTO to Entity
        Category category = toEntity(request, userId);

        // 3. Handle parent relationship if parentId is provided
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));

            // Verify parent belongs to same user
            if (!parent.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Parent category does not belong to user");
            }

            category.setParent(parent);
        }

        // 4. Save to database
        Category saved = categoryRepository.save(category);

        // 5. Convert back to DTO and return
        return toResponse(saved);
    }

    public CategoryResponse getCategoryById(UUID id, UUID userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to user");
        }

        return toResponse(category);
    }

    public List<CategoryResponse> getCategoriesByUserId(UUID userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);

        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getRootCategoriesByUserId(UUID userId) {
        List<Category> categories = categoryRepository.findByUserIdAndParentIsNull(userId);

        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getChildrenCategoriesByParentId(UUID parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);

        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(UUID id, UUID userId, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to user");
        }

        // Check if name already exists (excluding current category)
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setDescription(request.getDescription());

        // Handle parent relationship
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));

            // Verify parent belongs to same user
            if (!parent.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Parent category does not belong to user");
            }

            // Prevent circular reference (category cannot be its own parent)
            if (parent.getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updated = categoryRepository.save(category);

        return toResponse(updated);
    }

    public void deleteCategory(UUID id, UUID userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to user");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .icon(category.getIcon())
                .color(category.getColor())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private Category toEntity(CategoryRequest request, UUID userId) {
        return Category.builder()
                .name(request.getName())
                .userId(userId)
                .icon(request.getIcon())
                .color(request.getColor())
                .description(request.getDescription())
                .build();
    }
}
