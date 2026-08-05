-- Stands in for scripts/db-provision-roles.sh: the schema is created by provisioning,
-- never by a migration, so the service's changelog assumes it already exists.
create schema if not exists user_service;
