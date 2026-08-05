#!/usr/bin/env bash
#
# db-provision-roles.sh — one Postgres role per service, granted on its own schema.
#
# The services share a Postgres instance and are separated by schema. That separation
# is only real if the database enforces it: without these grants nothing stops a JOIN
# across schemas, and the boundary is a naming convention that fails the day someone
# needs data from a neighbour.
#
# Usage: scripts/db-provision-roles.sh
#
set -euo pipefail

DB_HOST="${DB_HOST:-postgres.adventistportal.orb.local}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-adventistportal}"
DB_ADMIN="${DB_ADMIN:-postgres}"
DB_ADMIN_PASSWORD="${DB_ADMIN_PASSWORD:-postgres}"

PG_IMAGE="postgres:16-alpine"
SERVICES="notification user chat inventory"

log() { printf '\033[1;36m[db-roles]\033[0m %s\n' "$*"; }

sql=""
for service in $SERVICES; do
  role="${service}_service"
  schema="${service}_service"
  sql="$sql
DO \$\$ BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${role}') THEN
    CREATE ROLE ${role} LOGIN PASSWORD '${role}';
  END IF;
END \$\$;
CREATE SCHEMA IF NOT EXISTS ${schema};
GRANT USAGE, CREATE ON SCHEMA ${schema} TO ${role};
GRANT ALL ON ALL TABLES IN SCHEMA ${schema} TO ${role};
GRANT ALL ON ALL SEQUENCES IN SCHEMA ${schema} TO ${role};
ALTER DEFAULT PRIVILEGES IN SCHEMA ${schema} GRANT ALL ON TABLES TO ${role};
ALTER DEFAULT PRIVILEGES IN SCHEMA ${schema} GRANT ALL ON SEQUENCES TO ${role};
-- Liquibase keeps its ledger in the service's own schema, so it needs nothing in public.
"
done

log "Creating a role per service on $DB_NAME"
docker run --rm -e PGPASSWORD="$DB_ADMIN_PASSWORD" "$PG_IMAGE" \
  psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_ADMIN" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q -c "$sql"

log "Done. Each role can reach its own schema and no other."
