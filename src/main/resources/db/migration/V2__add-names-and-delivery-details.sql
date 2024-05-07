ALTER TABLE main_service.users
    ADD COLUMN if not exists first_name character varying,
    ADD COLUMN if not exists last_name  character varying;
ALTER TABLE main_service.address_deliveries
    ADD COLUMN if not exists delivery_details character varying;