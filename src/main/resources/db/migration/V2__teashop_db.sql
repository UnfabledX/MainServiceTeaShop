ALTER TABLE main_service.users
    ADD COLUMN first_name character varying,
    ADD COLUMN last_name  character varying;
ALTER TABLE main_service.address_deliveries
    ADD COLUMN delivery_details character varying;