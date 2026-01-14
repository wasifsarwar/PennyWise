# Pennywise - Project Context for Code Reviews

## Project Overview

**Pennywise** is a personal finance tracker application built with:
- **Backend**: Spring Boot 4.0.1 + Java 21
- **Database**: PostgreSQL 16 (via Docker)
- **ORM**: Spring Data JPA + Hibernate
- **Migrations**: Flyway
- **Build Tool**: Gradle (Kotlin DSL)

## Architecture Pattern

This project follows a **layered architecture** with **package-by-feature** organization:

```
com.pennywise.backend
├── config/          # Application-wide configuration
├── common/           # Shared code (DTOs, exceptions)
├── {domain}/         # Feature packages (user, category, transaction, etc.)
│   ├── entity/       # JPA entities
│   ├── repository/   # Spring Data JPA repositories
│   ├── service/      # Business logic
│   ├── controller/   # REST API endpoints
│   └── dto/          # Data Transfer Objects
```

## Code Conventions

### Entity Layer
- Use Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor(access = AccessLevel.PRIVATE)`, `@Builder`
- Always use `@Entity` and `@Table(name = "...")` explicitly
- Use `@CreationTimestamp` and `@UpdateTimestamp` for audit fields
- Relationships: Use `FetchType.LAZY` by default
- Use `@JsonIgnore` on bidirectional relationships to prevent circular JSON

### Repository Layer
- Extend `JpaRepository<Entity, Long>`
- Use Spring Data JPA query method naming conventions
- Custom queries use `@Query` annotation with JPQL
- Always verify ownership (userId) in queries

### Service Layer
- Mark with `@Service` and `@Transactional`
- Use `@RequiredArgsConstructor` for dependency injection
- Always validate user ownership before operations
- Convert between Entity ↔ DTO (never expose entities directly)
- Throw `ResourceNotFoundException` for missing resources
- Throw `IllegalArgumentException` for business rule violations

### Controller Layer
- Mark with `@RestController` and `@RequestMapping("/api/{resource}")`
- Use `@RequiredArgsConstructor` for dependency injection
- Always wrap responses in `ApiResponse<T>`
- Use `@Valid` on `@RequestBody` parameters
- Use proper HTTP status codes:
  - `201 CREATED` for POST
  - `200 OK` for GET/PUT
  - `204 NO_CONTENT` for DELETE
- Temporary: Use `@RequestHeader("X-User-Id")` for user identification (will be replaced with JWT)

### DTOs
- **Request DTOs**: Include validation annotations (`@NotNull`, `@Size`, `@Pattern`)
- **Response DTOs**: Use `@JsonInclude(JsonInclude.Include.NON_NULL)`
- Never expose internal entity fields (like internal IDs, audit fields unless needed)

## Common Patterns

### User Ownership Validation
Every service method that accesses user data must verify ownership:
```java
if (!entity.getUserId().equals(userId)) {
    throw new IllegalArgumentException("Resource does not belong to user");
}
```

### Entity to DTO Conversion
Always use helper methods in service layer:
```java
private CategoryResponse toResponse(Category category) {
    return CategoryResponse.builder()
        .id(category.getId())
        // ... map all fields
        .build();
}
```

### Error Handling
- Use `ResourceNotFoundException` for 404s (handled by `GlobalExceptionHandler`)
- Use `IllegalArgumentException` for business rule violations (returns 400)
- Validation errors are automatically handled by `@Valid` + `GlobalExceptionHandler`

## Security Considerations

### Current State (Development)
- Security is disabled for development (`SecurityConfig` allows all requests)
- User ID passed via `X-User-Id` header (temporary)
- TODO: Implement JWT authentication before production

### Future Security Requirements
- All endpoints (except `/api/auth/**`) must require authentication
- User ID must come from JWT token, not header
- Validate user ownership on every request
- Use `@PreAuthorize` or method-level security

## Database Patterns

### Migrations
- Use Flyway migrations in `src/main/resources/db/migration/`
- Naming: `V{version}__{description}.sql` (double underscore!)
- Never modify existing migrations - create new ones
- Always include indexes for foreign keys and frequently queried columns

### Entity Relationships
- Use `@ManyToOne` for parent relationships
- Use `@OneToMany` with `mappedBy` for children
- Always set `cascade` appropriately (usually `CascadeType.ALL` for parent-child)
- Use `ON DELETE SET NULL` or `CASCADE` based on business rules

## Things to Watch For

### Common Bugs
1. **Missing ownership validation** - Always check `userId` matches
2. **Circular references** - Use `@JsonIgnore` on bidirectional relationships
3. **N+1 query problems** - Use `@EntityGraph` or `JOIN FETCH` when needed
4. **Transaction boundaries** - Service methods should be `@Transactional`
5. **Null pointer exceptions** - Check for null before accessing relationships
6. **Validation bypass** - Always use `@Valid` on request DTOs

### Performance Issues
- Avoid loading entire collections when only count is needed
- Use pagination for list endpoints (not yet implemented)
- Index foreign keys and frequently queried columns
- Use `FetchType.LAZY` by default, `EAGER` only when necessary

### Code Smells
- Entities exposed directly in controllers (should use DTOs)
- Business logic in controllers (should be in services)
- Raw SQL in repositories (prefer JPQL or query methods)
- Missing validation on request DTOs
- Hardcoded user IDs (should come from authentication)

## Testing Expectations

### Current State
- Basic test structure exists (`BackendApplicationTests`)
- No comprehensive test coverage yet

### Future Testing Requirements
- Unit tests for service layer (mock repositories)
- Integration tests for controllers (use `@WebMvcTest`)
- Repository tests with `@DataJpaTest`
- Use Testcontainers for database tests

## Domain-Specific Rules

### Categories
- Category names must be unique per user
- Parent category must belong to same user
- Cannot set category as its own parent (circular reference)
- Deleting parent should handle children (currently CASCADE)

### Transactions (Future)
- Must belong to a user
- Must have an account
- Must have a category
- Amount can be positive (income) or negative (expense)

### Accounts (Future)
- Must belong to a user
- Balance should be calculated from transactions (not stored)

### Budgets (Future)
- Must belong to a user
- Must reference a category
- Must have a time period (monthly, weekly, etc.)

## API Response Format

All API responses use the `ApiResponse<T>` wrapper:

```json
{
  "success": true,
  "message": "Optional message",
  "data": { /* actual response data */ },
  "timestamp": "2026-01-14T10:00:00Z"
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "timestamp": "2026-01-14T10:00:00Z"
}
```

## Dependencies

### Key Libraries
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security (currently disabled)
- Lombok
- Flyway
- PostgreSQL Driver
- Validation API

### Build Configuration
- Java 21
- Gradle with Kotlin DSL
- Kotlin version managed by Spring Boot

## Development Workflow

1. **Database Changes**: Create Flyway migration first
2. **Entity**: Map to database table
3. **Repository**: Add query methods
4. **DTOs**: Create request/response DTOs
5. **Service**: Implement business logic
6. **Controller**: Expose REST endpoints

## Notes for Reviewers

- This is a learning project - focus on teaching best practices
- Code should be production-ready patterns, even if features are incomplete
- Pay special attention to security implications
- Ensure proper error handling and validation
- Check for proper use of Spring Boot conventions
- Verify transaction boundaries are correct
- Ensure DTOs are used consistently (no entity leakage)

