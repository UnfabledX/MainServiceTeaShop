-- Switch to 'teashop' database
\c teashop;

-- Create schema 'main_service' if it doesn't exist
CREATE SCHEMA IF NOT EXISTS main_service;

CREATE EXTENSION if not exists pg_trgm with schema main_service;

ALTER DATABASE teashop SET pg_trgm.word_similarity_threshold = 0.55;