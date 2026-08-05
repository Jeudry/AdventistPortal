--liquibase formatted sql

--changeset sargon:04-02-outbox-chat
--comment: The outbox. An event is written here inside the transaction that caused it and
--comment: sent to the broker afterwards, so the domain change and the announcement of it
--comment: can never disagree.

create table chat_service.outbox (
    id uuid not null primary key,
    exchange varchar(255) not null,
    routing_key varchar(255) not null,
    proto_type varchar(255) not null,
    payload bytea not null,
    created_at timestamp(6) with time zone not null default now(),
    sent_at timestamp(6) with time zone,
    attempts integer not null default 0,
    last_error text
);

--comment: Partial: the relay only looks for what has not been sent, and that set stays
--comment: small however large the table grows.
create index idx_outbox_pending on chat_service.outbox (created_at) where sent_at is null;
