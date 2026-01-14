package com.pennywise.backend.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private String color;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryResponse> children; // Optional - for hierarchical display
}