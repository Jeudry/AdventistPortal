-- Stands in for scripts/db-provision-roles.sh: the schema is created by provisioning,
-- never by a migration, so this service's changelog assumes it already exists.
create schema if not exists chat_service;
