package com.pennywise.backend.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @Column(length = 50)
    @Size(max = 50, message = "Icon must be at most 50 characters")
    private String icon;

    @Column(length = 7)
    @Size(max = 7, message = "Color must be at most 7 characters")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g., #FF5733)")
    private String color;

    @Column(length = 255)
    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    // Helper method to check if this is a root category
    public boolean isRoot() {
        return parent == null;
    }

    // Helper method to add a child category
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    // Helper method to remove a child category
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
}
