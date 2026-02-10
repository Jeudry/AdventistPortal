ALTER TABLE inventory_service.articles
    ADD author_id VARCHAR(255);

ALTER TABLE inventory_service.articles
    ADD content TEXT;

ALTER TABLE inventory_service.articles
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE inventory_service.articles
    ADD description VARCHAR(500);

ALTER TABLE inventory_service.articles
    ADD image_url VARCHAR(255);

ALTER TABLE inventory_service.articles
    ADD rental_price DECIMAL;

ALTER TABLE inventory_service.articles
    ADD replacement_cost DECIMAL;

ALTER TABLE inventory_service.articles
    ADD sale_price DECIMAL;

ALTER TABLE inventory_service.articles
    ADD stock INTEGER;

ALTER TABLE inventory_service.articles
    ADD type SMALLINT;

ALTER TABLE inventory_service.articles
    ADD updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE inventory_service.articles
    ALTER COLUMN stock SET NOT NULL;

ALTER TABLE inventory_service.articles
    ALTER COLUMN type SET NOT NULL;