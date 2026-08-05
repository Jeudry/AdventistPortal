#!/usr/bin/env bash
#
# db-provision-roles.sh — apply scripts/sql/provision-roles.sql to a running database.
#
# The SQL is the single definition; under compose Postgres runs the same file itself on
# first boot. This script is for a database that already exists.
#
# Usage: scripts/db-provision-roles.sh [--yes]
#
set -euo pipefail

DB_HOST="${DB_HOST:-postgres.adventistportal.orb.local}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-adventistportal}"
DB_ADMIN="${DB_ADMIN:-postgres}"
DB_ADMIN_PASSWORD="${DB_ADMIN_PASSWORD:-postgres}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PG_IMAGE="postgres:16-alpine"

log() { printf '\033[1;36m[db-roles]\033[0m %s\n' "$*"; }

log "Creating a role per service on $DB_NAME"
docker run --rm -i -e PGPASSWORD="$DB_ADMIN_PASSWORD" "$PG_IMAGE" \
  psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_ADMIN" -d "$DB_NAME" -v ON_ERROR_STOP=1 -q \
  < "$ROOT/scripts/sql/provision-roles.sql"

log "Done. Each role can reach its own schema and no other."
