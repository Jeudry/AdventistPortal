-- One Postgres role per service, granted on its own schema.
--
-- The services share a Postgres instance and are separated by schema. That separation is
-- only real if the database enforces it: without these grants nothing stops a JOIN across
-- schemas, and the boundary is a naming convention that fails the day someone needs data
-- from a neighbour.
--
-- Creating a schema is provisioning, not migration, which is why it happens here and not
-- in any service's changelog. The roles have no CREATE on the database, so a service
-- cannot make itself a second home.
--
-- Run by scripts/db-provision-roles.sh against an existing database, and by Postgres
-- itself on first boot under compose. One definition, both paths.

DO $$
DECLARE
    service text;
BEGIN
    FOREACH service IN ARRAY ARRAY['notification_service', 'user_service', 'chat_service', 'inventory_service']
    LOOP
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = service) THEN
            EXECUTE format('CREATE ROLE %I LOGIN PASSWORD %L', service, service);
        END IF;

        EXECUTE format('CREATE SCHEMA IF NOT EXISTS %I', service);
        EXECUTE format('GRANT USAGE, CREATE ON SCHEMA %I TO %I', service, service);
        EXECUTE format('GRANT ALL ON ALL TABLES IN SCHEMA %I TO %I', service, service);
        EXECUTE format('GRANT ALL ON ALL SEQUENCES IN SCHEMA %I TO %I', service, service);
        EXECUTE format(
            'ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON TABLES TO %I', service, service);
        EXECUTE format(
            'ALTER DEFAULT PRIVILEGES IN SCHEMA %I GRANT ALL ON SEQUENCES TO %I', service, service);
    END LOOP;
END $$;
