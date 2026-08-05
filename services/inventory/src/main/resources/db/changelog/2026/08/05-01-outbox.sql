--liquibase formatted sql

--changeset sargon:05-01-outbox-inventory
--comment: The outbox. This service publishes nothing today, but it has the same relay as
--comment: the others, and a relay with no table to drain is a silence that looks like
--comment: success the day someone does publish.

create table inventory_service.outbox (
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
create index idx_outbox_pending on inventory_service.outbox (created_at) where sent_at is null;
