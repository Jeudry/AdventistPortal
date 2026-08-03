#!/usr/bin/env bash
#
# db-reset.sh — return the LOCAL dev database to zero so Liquibase rebuilds it.
#
# Drops the *_service schemas and Liquibase's ledger (databasechangelog /
# databasechangeloglock). The next app start reapplies the master changelog from
# scratch, giving a schema that matches the migrations exactly — no rollback
# statements needed, and no checksum conflict from editing a changeset you had
# already applied.
#
# Note this does NOT use `liquibase drop-all`: that command empties the schemas
# but leaves them standing, and the baseline issues a bare `create schema`, so
# the next start would fail with "schema already exists".
#
# This DESTROYS all local data. It refuses to run against a non-local host.
#
# Usage:   scripts/db-reset.sh [--yes]
#          DB_PORT=5435 DB_NAME=other scripts/db-reset.sh --yes
#
# Requires: Docker.
#
set -euo pipefail

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-adventistportal}"
DB_USER="${DB_USER:-adventistportal_user}"
DB_PASS="${DB_PASS:-adventistportal_password}"

PG_IMAGE="postgres:16-alpine"
SCHEMAS="user_service, inventory_service, chat_service, notification_service, quote_service"

log() { printf '\033[1;36m[db-reset]\033[0m %s\n' "$*"; }
die() { printf '\033[1;31m[db-reset]\033[0m %s\n' "$*" >&2; exit 1; }

case "$DB_HOST" in
  localhost|127.0.0.1) ;;
  *) die "refusing to run against a non-local host: $DB_HOST" ;;
esac

if [ "${1:-}" != "--yes" ]; then
  echo
  echo "  Esto BORRA todos los datos de $DB_USER@$DB_HOST:$DB_PORT/$DB_NAME"
  echo "  Schemas: $SCHEMAS"
  echo
  read -r -p "  Escribe 'si' para continuar: " answer
  [ "$answer" = "si" ] || die "cancelado"
fi

log "Dropping the *_service schemas and Liquibase's ledger in $DB_NAME"
docker run --rm -e PGPASSWORD="$DB_PASS" "$PG_IMAGE" \
  psql -h host.docker.internal -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q \
  -c "DROP SCHEMA IF EXISTS ${SCHEMAS} CASCADE;" \
  -c "DROP TABLE IF EXISTS public.databasechangelog, public.databasechangeloglock;"

log "Done. Start the app and Liquibase will rebuild the schema from the changelogs."
