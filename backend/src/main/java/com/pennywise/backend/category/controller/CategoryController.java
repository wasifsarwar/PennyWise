package com.pennywise.backend.category.controller;

import com.pennywise.backend.category.dto.CategoryRequest;
import com.pennywise.backend.category.dto.CategoryResponse;
import com.pennywise.backend.category.service.CategoryService;
import com.pennywise.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category = categoryService.createCategory(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestHeader("X-User-Id") UUID userId) {

        List<CategoryResponse> categories = categoryService.getCategoriesByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(
            @RequestHeader("X-User-Id") UUID userId) {

        List<CategoryResponse> categories = categoryService.getRootCategoriesByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {

        CategoryResponse category = categoryService.getCategoryById(id, userId);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getChildrenCategories(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {

        // Verify category exists and belongs to user
        categoryService.getCategoryById(id, userId);

        List<CategoryResponse> children = categoryService.getChildrenCategoriesByParentId(id);

        return ResponseEntity.ok(ApiResponse.success(children));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category = categoryService.updateCategory(id, userId, request);

        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {

        categoryService.deleteCategory(id, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Category deleted successfully"));
    }
}
