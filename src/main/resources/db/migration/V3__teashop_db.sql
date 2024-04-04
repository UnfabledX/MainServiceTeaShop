ALTER TABLE main_service.products
    DROP COLUMN image_id,
    ADD COLUMN status varchar(255) check (status in ('PRESENT', 'DELETED'));

create table if not exists main_service.images
(
    image_id   bigint not null unique,
    product_id bigint not null,
    primary key (image_id),
    CONSTRAINT fk_product_image
        FOREIGN KEY (product_id)
            REFERENCES main_service.products (id)
)