CREATE INDEX if not exists name_description_idx ON main_service.products
    USING GIN (name main_service.gin_trgm_ops, description main_service.gin_trgm_ops);
