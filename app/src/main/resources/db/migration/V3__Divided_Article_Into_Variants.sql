-- 1. Create new tables
CREATE TABLE inventory_service.categories
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    icon_name   VARCHAR(255),
    is_active   BOOLEAN      DEFAULT TRUE,
    parent_id   UUID,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE inventory_service.article_variants
(
    id               UUID         NOT NULL,
    sku              VARCHAR(255) NOT NULL,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    image_url        VARCHAR(255),
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    stock            INTEGER      NOT NULL DEFAULT 0,
    rental_price     DECIMAL(19, 4),
    sale_price       DECIMAL(19, 4),
    replacement_cost DECIMAL(19, 4),
    article_id       UUID,
    CONSTRAINT pk_article_variants PRIMARY KEY (id)
);

CREATE TABLE inventory_service.article_variant_attributes
(
    variant_id      UUID         NOT NULL,
    attribute_value VARCHAR(255),
    attribute_key   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_article_variant_attributes PRIMARY KEY (variant_id, attribute_key)
);

CREATE TABLE inventory_service.article_variant_dimensions
(
    variant_id UUID NOT NULL,
    label      VARCHAR(255),
    width_cm   DOUBLE PRECISION,
    height_cm  DOUBLE PRECISION,
    depth_cm   DOUBLE PRECISION,
    weight_kg  DOUBLE PRECISION
);

-- 2. Restructure 'articles' table
-- Add only NEW columns (category_id, name_template, description_template, is_active)
-- created_at, updated_at, and type are already in V2
ALTER TABLE inventory_service.articles ADD category_id UUID;
ALTER TABLE inventory_service.articles ADD name_template VARCHAR(255);
ALTER TABLE inventory_service.articles ADD description_template TEXT;
ALTER TABLE inventory_service.articles ADD is_active BOOLEAN DEFAULT TRUE;

-- Migrate data from old columns to new ones
UPDATE inventory_service.articles SET name_template = name WHERE name IS NOT NULL;
UPDATE inventory_service.articles SET description_template = description WHERE description IS NOT NULL;

-- Drop old columns that moved to variants or are no longer used
ALTER TABLE inventory_service.articles DROP COLUMN name;
ALTER TABLE inventory_service.articles DROP COLUMN description;
ALTER TABLE inventory_service.articles DROP COLUMN code;
ALTER TABLE inventory_service.articles DROP COLUMN stock;
ALTER TABLE inventory_service.articles DROP COLUMN rental_price;
ALTER TABLE inventory_service.articles DROP COLUMN sale_price;
ALTER TABLE inventory_service.articles DROP COLUMN replacement_cost;
ALTER TABLE inventory_service.articles DROP COLUMN image_url;
ALTER TABLE inventory_service.articles DROP COLUMN content;

-- Set constraints for new columns
ALTER TABLE inventory_service.articles ALTER COLUMN name_template SET NOT NULL;
ALTER TABLE inventory_service.articles ALTER COLUMN is_active SET NOT NULL;

-- 3. Add Foreign Keys
ALTER TABLE inventory_service.articles
    ADD CONSTRAINT FK_ARTICLES_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES inventory_service.categories (id);

ALTER TABLE inventory_service.article_variants
    ADD CONSTRAINT FK_ARTICLE_VARIANTS_ON_ARTICLE FOREIGN KEY (article_id) REFERENCES inventory_service.articles (id);

ALTER TABLE inventory_service.categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES inventory_service.categories (id);

ALTER TABLE inventory_service.article_variant_attributes
    ADD CONSTRAINT fk_article_variant_attributes_on_article_variant FOREIGN KEY (variant_id) REFERENCES inventory_service.article_variants (id);

ALTER TABLE inventory_service.article_variant_dimensions
    ADD CONSTRAINT fk_article_variant_dimensions_on_article_variant FOREIGN KEY (variant_id) REFERENCES inventory_service.article_variants (id);