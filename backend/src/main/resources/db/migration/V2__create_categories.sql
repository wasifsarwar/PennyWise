-- V2: Create categories table
-- Categories support hierarchical structure (parent/child relationships)
-- Each user has their own set of categories with unique names

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT,
    icon VARCHAR(50),
    color VARCHAR(7), -- Hex color code (e.g., #FF5733)
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key to users table
    CONSTRAINT fk_category_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    -- Self-referencing foreign key for parent category
    CONSTRAINT fk_category_parent 
        FOREIGN KEY (parent_id) 
        REFERENCES categories(id) 
        ON DELETE SET NULL,
    
    -- Unique constraint: category names must be unique per user
    CONSTRAINT uk_category_user_name 
        UNIQUE (user_id, name)
);

-- Index on user_id for fast lookups (you'll always query by user)
CREATE INDEX idx_categories_user_id ON categories(user_id);

-- Index on parent_id for fast hierarchical queries
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

-- Index on (user_id, parent_id) for finding root categories or children
CREATE INDEX idx_categories_user_parent ON categories(user_id, parent_id);