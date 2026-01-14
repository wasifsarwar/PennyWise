-- ============================================
-- PostgreSQL Initialization Script
-- ============================================
-- This script runs automatically when the container is first created.
-- It's useful for setting up additional databases, users, or extensions.
-- ============================================

-- Enable useful extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- For fuzzy text search

-- Grant privileges (optional - main user already has full access)
-- GRANT ALL PRIVILEGES ON DATABASE pennywise TO pennywise;

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'Database initialization completed successfully';
END $$;

