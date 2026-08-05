--liquibase formatted sql

--changeset sargon:00-init-schema splitStatements:true endDelimiter:;
--comment: Complete initial schema, generated from the JPA model. Regenerate with scripts/gen-migration.sh after model changes.

create schema if not exists chat_service;
create schema if not exists inventory_service;
create table chat_service.chat_messages (id uuid not null, chat_id uuid not null, content TEXT not null, created_at timestamp(6) with time zone not null, sender_id uuid not null, primary key (id));
create table chat_service.chat_participants (user_id uuid not null, created_at timestamp(6) with time zone not null, email varchar(255) not null unique, profile_picture_url TEXT, username varchar(255) not null unique, primary key (user_id));
create table chat_service.chat_participants_cross_ref (chat_id uuid not null, participant_id uuid not null, constraint idx_chat_participant_chat_id_participant_id unique (chat_id, participant_id), constraint idx_chat_participant_participant_id_chat_id unique (participant_id, chat_id));
create table chat_service.chats (id uuid not null, created_at timestamp(6) with time zone not null, creator_id uuid not null, primary key (id));
create index idx_chat_message_chat_id_created_at on chat_service.chat_messages (chat_id, created_at desc);
create table inventory_service.article_variant_attributes (variant_id uuid not null, attribute_value varchar(255), attribute_key varchar(255) not null, primary key (variant_id, attribute_key));
create table inventory_service.article_variant_dimensions (variant_id uuid not null, dimensions_depth_cm float(53), dimensions_height_cm float(53), dimensions_label varchar(255), dimensions_weight_kg float(53), dimensions_width_cm float(53));
create table inventory_service.article_variants (id uuid not null, description TEXT, image_url varchar(255), is_active boolean not null, name varchar(255) not null, replacement_cost_cents bigint not null, sku varchar(255) not null, stock integer not null, article_id uuid, primary key (id));
create table inventory_service.articles (id uuid not null, created_at timestamp(6) with time zone, deleted_at timestamp(6) with time zone, description_template TEXT, is_active boolean not null, name_template varchar(255) not null, updated_at timestamp(6) with time zone, category_id uuid, primary key (id));
create table inventory_service.categories (id uuid not null, description TEXT, icon_name varchar(255), is_active boolean, name varchar(255) not null, parent_id uuid, primary key (id));
alter table if exists chat_service.chat_messages add constraint FKt56nsqjwt7t4sian6vts9wg3t foreign key (chat_id) references chat_service.chats on delete cascade;
alter table if exists chat_service.chat_messages add constraint FKra53n2lu9wk61w5j7ovxo4as7 foreign key (sender_id) references chat_service.chat_participants;
alter table if exists chat_service.chat_participants_cross_ref add constraint FKj2o12vq992gkwucpgab21nig2 foreign key (participant_id) references chat_service.chat_participants;
alter table if exists chat_service.chat_participants_cross_ref add constraint FKmgbllw57tuabb1cqsl9uacrjc foreign key (chat_id) references chat_service.chats;
alter table if exists chat_service.chats add constraint FKny26by9vuy9u6f90rxq42ul6s foreign key (creator_id) references chat_service.chat_participants;
alter table if exists inventory_service.article_variant_attributes add constraint FKrppp3k2y98qqws74gmp8s6ans foreign key (variant_id) references inventory_service.article_variants;
alter table if exists inventory_service.article_variant_dimensions add constraint FK6f0rign72rshl774291r0wjrm foreign key (variant_id) references inventory_service.article_variants;
alter table if exists inventory_service.article_variants add constraint FK8fxh8by748n57pvnfnd4qgaf0 foreign key (article_id) references inventory_service.articles;
alter table if exists inventory_service.articles add constraint FK7i4rryg7kqwyyrr08temnc71e foreign key (category_id) references inventory_service.categories;
alter table if exists inventory_service.categories add constraint FKsaok720gsu4u2wrgbk10b5n8d foreign key (parent_id) references inventory_service.categories;
