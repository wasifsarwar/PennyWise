package com.pennywise.backend.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotNull(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;

    private UUID parentId; // Optional - null means root category

    @Size(max = 50, message = "Icon must be at most 50 characters")
    private String icon;

    @Size(max = 7, message = "Color must be at most 7 characters")
    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF5733)")
    private String color;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}