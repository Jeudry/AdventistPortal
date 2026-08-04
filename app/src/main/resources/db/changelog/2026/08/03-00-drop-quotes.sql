--liquibase formatted sql

--changeset sargon:03-00-drop-quotes
--comment: The quotes feature is gone. This also removes the only foreign key that crossed a service boundary (quote_items.variant_id -> inventory_service.article_variants).
drop schema if exists quote_service cascade;
--rollback create schema quote_service;
--rollback create table quote_service.quotes (id uuid not null, client_id uuid not null, created_at timestamp(6) with time zone, event_end_date timestamp(6) with time zone not null, event_start_date timestamp(6) with time zone not null, status varchar(255) not null check ((status in ('DRAFT','PENDING_REVIEW','QUOTED','RESERVED','CANCELLED','EXPIRED'))), venue_address varchar(255), primary key (id));
--rollback create table quote_service.quote_items (id uuid not null, added_at timestamp(6) with time zone, quantity integer not null, unit_price numeric(19,4) not null, quote_id uuid, variant_id uuid, primary key (id));
--rollback alter table quote_service.quote_items add constraint FKrvsmoef7yontnlu1lwxrb0g3g foreign key (quote_id) references quote_service.quotes;
--rollback alter table quote_service.quote_items add constraint FKl0hqi2imbpnkx17atoqhur1x5 foreign key (variant_id) references inventory_service.article_variants;
