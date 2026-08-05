--liquibase formatted sql

--changeset sargon:05-02-outbox-trace-user
--comment: The trace context of the request that produced the event. The relay sends long
--comment: after that request has finished, so without this the trace stops at the commit
--comment: and the consumer's work looks like something nobody asked for.

alter table user_service.outbox add column trace_parent varchar(64);
