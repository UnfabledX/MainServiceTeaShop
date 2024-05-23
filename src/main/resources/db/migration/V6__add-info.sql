CREATE TABLE IF NOT EXISTS main_service.info
(
    id                bigserial,
    contentUA         character varying NOT NULL,
    contentEU         character varying NOT NULL,
    PRIMARY KEY (id)
);
