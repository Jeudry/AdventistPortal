--liquibase formatted sql

--changeset sargon:05-00-init-inventory
--comment: The tables this service owns. The schema itself is provisioning, not
--comment: migration: scripts/db-provision-roles.sh creates it, and the service role has
--comment: no CREATE on the database.

create table inventory_service.article_variant_attributes (variant_id uuid not null, attribute_value varchar(255), attribute_key varchar(255) not null, primary key (variant_id, attribute_key));
create table inventory_service.article_variant_dimensions (variant_id uuid not null, dimensions_depth_cm float(53), dimensions_height_cm float(53), dimensions_label varchar(255), dimensions_weight_kg float(53), dimensions_width_cm float(53));
create table inventory_service.article_variants (id uuid not null, description TEXT, image_url varchar(255), is_active boolean not null, name varchar(255) not null, replacement_cost_cents bigint not null, sku varchar(255) not null, stock integer not null, article_id uuid, primary key (id));
create table inventory_service.articles (id uuid not null, created_at timestamp(6) with time zone, deleted_at timestamp(6) with time zone, description_template TEXT, is_active boolean not null, name_template varchar(255) not null, updated_at timestamp(6) with time zone, category_id uuid, primary key (id));
create table inventory_service.categories (id uuid not null, description TEXT, icon_name varchar(255), is_active boolean, name varchar(255) not null, parent_id uuid, primary key (id));
alter table if exists inventory_service.article_variant_attributes add constraint FKrppp3k2y98qqws74gmp8s6ans foreign key (variant_id) references inventory_service.article_variants;
alter table if exists inventory_service.article_variant_dimensions add constraint FK6f0rign72rshl774291r0wjrm foreign key (variant_id) references inventory_service.article_variants;
alter table if exists inventory_service.article_variants add constraint FK8fxh8by748n57pvnfnd4qgaf0 foreign key (article_id) references inventory_service.articles;
alter table if exists inventory_service.articles add constraint FK7i4rryg7kqwyyrr08temnc71e foreign key (category_id) references inventory_service.categories;
alter table if exists inventory_service.categories add constraint FKsaok720gsu4u2wrgbk10b5n8d foreign key (parent_id) references inventory_service.categories;
