--liquibase formatted sql

--changeset sargon:03-01-church-inventory
--comment: The inventory now tracks church property, not goods for rent or sale. Drops the discounts feature and the commercial fields; replacement_cost stays as the asset value for insurance and replacement records.
drop table if exists inventory_service.discounts;
alter table inventory_service.articles drop column if exists type;
alter table inventory_service.article_variants drop column if exists rental_price;
alter table inventory_service.article_variants drop column if exists sale_price;
--rollback create table inventory_service.discounts (id uuid not null, created_at timestamp(6) with time zone, description TEXT, end_date timestamp(6) with time zone, is_active boolean not null, name varchar(255) not null, priority integer not null, start_date timestamp(6) with time zone, target_article_id uuid, target_category_id uuid, target_variant_id uuid, type varchar(255) not null check ((type in ('PERCENTAGE','FIXED_AMOUNT'))), updated_at timestamp(6) with time zone, value numeric(19,4) not null, primary key (id));
--rollback alter table inventory_service.articles add column type varchar(255) not null default 'Rental' check ((type in ('Rental','Sale')));
--rollback alter table inventory_service.article_variants add column rental_price numeric(19,4);
--rollback alter table inventory_service.article_variants add column sale_price numeric(19,4);
