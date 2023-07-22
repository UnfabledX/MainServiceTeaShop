CREATE SCHEMA IF NOT EXISTS teashop_db;

CREATE TABLE IF NOT EXISTS teashop_db.address_deliveries
(
    id                bigserial         NOT NULL,
    country           character varying NOT NULL,
    region            character varying,
    city              character varying NOT NULL,
    operator_name     character varying NOT NULL,
    number_department integer           NOT NULL,
    zip_code          integer           NOT NULL,
    PRIMARY KEY (id)

);

CREATE TABLE IF NOT EXISTS teashop_db.users
(
    id         bigserial         NOT NULL,
    user_name  character varying NOT NULL,
    password   character varying NOT NULL,
    email      character varying NOT NULL,
    phone      character varying,
    role       character varying NOT NULL,
    address_id bigserial,
    birthday   date,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    UNIQUE (user_name, email, phone),
    PRIMARY KEY (id),
    CONSTRAINT fk_address
        FOREIGN KEY (address_id)
            REFERENCES teashop_db.address_deliveries (id)
);

CREATE TABLE IF NOT EXISTS teashop_db.products
(
    id                bigserial         NOT NULL,
    name              character varying NOT NULL,
    description       character varying NOT NULL,
    price             decimal NOT NULL,
    PRIMARY KEY (id)
);