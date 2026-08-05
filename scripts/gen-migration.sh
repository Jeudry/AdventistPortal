#!/usr/bin/env bash
#
# gen-migration.sh — EF-Core-style migration generator (offline, no real DB).
#
# Diffs the current JPA model against the state produced by the Liquibase master
# changelog and emits a delta changelog — exactly like `dotnet ef migrations add`
# diffing the model against the ModelSnapshot. Nothing here touches your Supabase,
# local or orb databases: it spins a throwaway Postgres, uses it, and tears it down.
#
# Usage:   scripts/gen-migration.sh <MigrationName> <service>
# Example: scripts/gen-migration.sh AddArticleDeletedAt inventory
#
# One service at a time: each owns its own schema and its own changelog, so a model
# and the migrations it is diffed against always belong to the same service.
#
# Requires: Docker, the Gradle wrapper, and a resolved Postgres JDBC driver in the
# Gradle cache (any prior build pulls it).
#
set -euo pipefail

NAME="${1:-migration}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SERVICE="${2:?usage: gen-migration.sh <MigrationName> <service>}"
RESOURCES="$ROOT/services/$SERVICE/src/main/resources"
CHANGELOG_DIR="$RESOURCES/db/changelog"
MASTER_REL="db/changelog/db.changelog-master.xml"
MODEL_SQL="$ROOT/services/$SERVICE/build/model-schema.sql"

CONTAINER="ap-shadow-migrate"          # our own throwaway; never a user container
PORT="5433"                            # avoid the dev stack on 5432
PGURL="jdbc:postgresql://host.docker.internal:${PORT}"
LB_IMAGE="liquibase/liquibase:5.0.3"   # matches the Spring-managed Liquibase version
SCHEMAS="user_service,inventory_service,chat_service,notification_service,quote_service"

log() { printf '\033[1;36m[gen-migration]\033[0m %s\n' "$*"; }

cleanup() { docker stop "$CONTAINER" >/dev/null 2>&1 || true; }
trap cleanup EXIT

# --- 1. export the JPA model to DDL (offline, no DB) -------------------------
log "Exporting the JPA model of $SERVICE -> services/$SERVICE/build/model-schema.sql"
"$ROOT/gradlew" -p "$ROOT" ":services:$SERVICE:exportModelSchema" -q --console=plain
[ -s "$MODEL_SQL" ] || { echo "model schema not generated"; exit 1; }

# --- 2. locate the Postgres JDBC driver in the Gradle cache -----------------
PG_DRIVER="$(find "$HOME/.gradle/caches" -name 'postgresql-*.jar' \
  ! -name '*sources*' ! -name '*javadoc*' 2>/dev/null | sort | tail -1)"
[ -n "$PG_DRIVER" ] || { echo "Postgres JDBC driver not found in Gradle cache; run a build first"; exit 1; }

# --- 3. throwaway Postgres with two databases -------------------------------
log "Starting throwaway Postgres ($CONTAINER) on :$PORT"
cleanup
docker run -d --rm --name "$CONTAINER" \
  -e POSTGRES_USER=ap -e POSTGRES_PASSWORD=pw -e POSTGRES_DB=ap \
  -p "127.0.0.1:${PORT}:5432" postgres:16-alpine >/dev/null
until docker exec "$CONTAINER" pg_isready -U ap >/dev/null 2>&1; do sleep 1; done
docker exec "$CONTAINER" psql -U ap -d ap -q \
  -c "CREATE DATABASE current_db;" -c "CREATE DATABASE model_db;" >/dev/null

# --- 4. current_db  = apply the existing master changelog -------------------
log "Applying master changelog -> current_db (the 'snapshot' side)"
docker run --rm -v "$PG_DRIVER":/liquibase/lib/postgresql.jar \
  -v "$RESOURCES":/rsc "$LB_IMAGE" \
  --search-path=/rsc \
  --url="${PGURL}/current_db" --username=ap --password=pw \
  --changelog-file="${MASTER_REL}" update >/dev/null

# --- 5. model_db    = load the freshly-exported model DDL -------------------
log "Loading model DDL -> model_db (the 'current model' side)"
docker exec -i "$CONTAINER" psql -U ap -d model_db -q < "$MODEL_SQL" >/dev/null 2>&1 || true

# --- 6. diff current_db -> model_db  => the delta changelog ------------------
mkdir -p "$CHANGELOG_DIR/generated"
STAMP="$(date +%Y%m%d%H%M%S)"
OUT_REL="generated/${STAMP}-${NAME}.postgresql.sql"
log "Diffing current_db -> model_db => db/changelog/${OUT_REL}"
# NOTE: the OUTPUT changelog-file must be an absolute path inside the mounted
# volume — with a relative path Liquibase writes it to the container CWD, which is
# lost when the container exits (--rm).
docker run --rm -v "$PG_DRIVER":/liquibase/lib/postgresql.jar \
  -v "$CHANGELOG_DIR":/out "$LB_IMAGE" \
  --url="${PGURL}/current_db" --username=ap --password=pw \
  --reference-url="${PGURL}/model_db" --reference-username=ap --reference-password=pw \
  --schemas="$SCHEMAS" \
  diff-changelog --changelog-file="/out/${OUT_REL}"

echo
if [ -s "$CHANGELOG_DIR/$OUT_REL" ] && grep -q '^-- changeset' "$CHANGELOG_DIR/$OUT_REL"; then
  log "Delta written: services/$SERVICE/src/main/resources/db/changelog/${OUT_REL}"
  cat <<EOF

  NEXT STEPS (like reviewing an EF migration before committing):
    1. REVIEW the file. Liquibase's modifyDataType direction is unreliable in 5.0.3,
       and renames show up as drop+add (data loss) — fix those by hand.
    2. Add it to the master changelog:
         <include file="${OUT_REL}" relativeToChangelogFile="true"/>
    3. Re-run this script; a clean run should now produce an EMPTY delta.
EOF
else
  log "No differences — the model already matches the applied changelog. Nothing to generate."
  rm -f "$CHANGELOG_DIR/$OUT_REL"
fi
