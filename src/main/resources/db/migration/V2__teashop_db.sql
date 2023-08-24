ALTER TABLE teashop_db.users
    ADD COLUMN first_name character varying,
    ADD COLUMN last_name  character varying;
ALTER TABLE teashop_db.address_deliveries
    ADD COLUMN delivery_details character varying;