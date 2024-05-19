ALTER TABLE main_service.products
    ADD COLUMN if not exists search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('simple', coalesce(teashop.main_service.products.name, '')), 'A') ||
            setweight(to_tsvector('simple', coalesce(teashop.main_service.products.description, '')), 'B')
            ) STORED;

CREATE INDEX search_idx ON main_service.products USING GIN (search_vector);

CREATE TABLE main_service.words AS SELECT word FROM
    ts_stat('SELECT search_vector FROM main_service.products');

CREATE INDEX words_idx ON main_service.words USING GIN (word gin_trgm_ops);