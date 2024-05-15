ALTER TABLE main_service.products
    ADD COLUMN if not exists search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('english', coalesce(teashop.main_service.products.name, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(teashop.main_service.products.description, '')), 'B')
            ) STORED;

CREATE INDEX search_idx ON main_service.products USING GIN (search_vector);
