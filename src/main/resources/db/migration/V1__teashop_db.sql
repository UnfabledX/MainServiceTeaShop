CREATE SCHEMA IF NOT EXISTS teashop_db;

CREATE TABLE IF NOT EXISTS teashop_db.address_deliveries
(
    id                bigserial,
    country           character varying NOT NULL,
    region            character varying,
    city              character varying NOT NULL,
    operator_name     character varying NOT NULL,
    number_department integer,
    zip_code          integer,
    PRIMARY KEY (id)

);

CREATE TABLE IF NOT EXISTS teashop_db.users
(
    id                  bigserial,
    user_name           character varying NOT NULL,
    password            character varying NOT NULL,
    verification_token  character varying,
    token_time          timestamp without time zone,
    email               character varying NOT NULL,
    phone               character varying,
    role                character varying NOT NULL,
    address_id          bigint,
    birthday            date,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    account_status      character varying NOT NULL,
    UNIQUE (user_name, email, phone),
    PRIMARY KEY (id),
    CONSTRAINT fk_address
        FOREIGN KEY (address_id)
            REFERENCES teashop_db.address_deliveries (id)
);

CREATE TABLE IF NOT EXISTS teashop_db.products
(
    id                bigserial,
    name              character varying NOT NULL,
    description       character varying NOT NULL,
    price_ua          decimal NOT NULL,
    price_eu          decimal NOT NULL,
    image_id          bigint,
    UNIQUE(name),
    PRIMARY KEY (id)
);