--liquibase formatted sql

--changeset sargon:05-00-init-chat
--comment: The tables this service owns. The schema itself is provisioning, not
--comment: migration: scripts/db-provision-roles.sh creates it, and the service role has
--comment: no CREATE on the database.

create table chat_service.chat_messages (id uuid not null, chat_id uuid not null, content TEXT not null, created_at timestamp(6) with time zone not null, sender_id uuid not null, primary key (id));
create table chat_service.chat_participants (user_id uuid not null, created_at timestamp(6) with time zone not null, email varchar(255) not null unique, profile_picture_url TEXT, username varchar(255) not null unique, primary key (user_id));
create table chat_service.chat_participants_cross_ref (chat_id uuid not null, participant_id uuid not null, constraint idx_chat_participant_chat_id_participant_id unique (chat_id, participant_id), constraint idx_chat_participant_participant_id_chat_id unique (participant_id, chat_id));
create table chat_service.chats (id uuid not null, created_at timestamp(6) with time zone not null, creator_id uuid not null, primary key (id));
create index idx_chat_message_chat_id_created_at on chat_service.chat_messages (chat_id, created_at desc);
alter table if exists chat_service.chat_messages add constraint FKt56nsqjwt7t4sian6vts9wg3t foreign key (chat_id) references chat_service.chats on delete cascade;
alter table if exists chat_service.chat_messages add constraint FKra53n2lu9wk61w5j7ovxo4as7 foreign key (sender_id) references chat_service.chat_participants;
alter table if exists chat_service.chat_participants_cross_ref add constraint FKj2o12vq992gkwucpgab21nig2 foreign key (participant_id) references chat_service.chat_participants;
alter table if exists chat_service.chat_participants_cross_ref add constraint FKmgbllw57tuabb1cqsl9uacrjc foreign key (chat_id) references chat_service.chats;
alter table if exists chat_service.chats add constraint FKny26by9vuy9u6f90rxq42ul6s foreign key (creator_id) references chat_service.chat_participants;
