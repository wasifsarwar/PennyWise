package com.pennywise.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private UUID id;
    private String name;
    private UUID parentId;
    private String icon;
    private String color;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryResponse> children; // Optional - for hierarchical display
}
