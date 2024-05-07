ALTER TABLE main_service.products
    ADD COLUMN if not exists type varchar(255) check (type in ('TEA', 'MUSHROOMS', 'JAMS')) default 'TEA';
