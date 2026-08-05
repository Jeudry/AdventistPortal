#!/usr/bin/env bash
#
# k8s-secrets.sh — create the cluster's Secret from your .env.
#
# The answer to "where do the secrets come from" once this is deployed: from here, into a
# Kubernetes Secret, and from there into the one pod that needs each. No file in this
# repository contains a value, and this script writes none — it pipes them to the API
# server and forgets them.
#
# Run it again to rotate: it replaces the Secret, and restarting the deployments picks the
# new values up.
#
# Usage: KUBECONFIG=... scripts/k8s-secrets.sh
#
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT/.env}"
NAMESPACE="${NAMESPACE:-adventistportal}"

log() { printf '\033[1;36m[secrets]\033[0m %s\n' "$*"; }
die() { printf '\033[1;31m[secrets]\033[0m %s\n' "$*" >&2; exit 1; }

[ -f "$ENV_FILE" ] || die "no $ENV_FILE — that is where the values live locally"

read_env() {
  # Deliberately not `source`: a .env is data, and executing it would run whatever is in it.
  local key="$1"
  local value
  value="$(grep -m1 "^${key}=" "$ENV_FILE" | cut -d= -f2- || true)"
  [ -n "$value" ] || die "missing $key in $ENV_FILE"
  printf '%s' "$value"
}

# One password per service role, matching what provision-roles.sql creates. They are equal
# to the role name today, which is fine for a database nothing outside the cluster can
# reach — and the place to change that is here, in one file, not across five deployments.
log "Writing the platform Secret into $NAMESPACE"
kubectl create secret generic platform-secrets \
  --namespace "$NAMESPACE" \
  --from-literal=JWT_PRIVATE_KEY="$(read_env JWT_PRIVATE_KEY)" \
  --from-literal=JWT_PUBLIC_KEY="$(read_env JWT_PUBLIC_KEY)" \
  --from-literal=API_KEY="$(read_env API_KEY)" \
  --from-literal=MAIL_FROM_EMAIL="$(read_env MAIL_FROM_EMAIL)" \
  --from-literal=MAIL_PASSWORD="$(read_env MAIL_PASSWORD)" \
  --from-literal=SUPABASE_SERVICE_KEY="${SUPABASE_SERVICE_KEY:-unused}" \
  --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres}" \
  --from-literal=REDIS_PASSWORD="${REDIS_PASSWORD:-adventistportal_redis_password}" \
  --from-literal=RABBITMQ_PASSWORD="${RABBITMQ_PASSWORD:-adventistportal_password}" \
  --from-literal=USER_DB_PASSWORD="${USER_DB_PASSWORD:-user_service}" \
  --from-literal=CHAT_DB_PASSWORD="${CHAT_DB_PASSWORD:-chat_service}" \
  --from-literal=INVENTORY_DB_PASSWORD="${INVENTORY_DB_PASSWORD:-inventory_service}" \
  --from-literal=NOTIFICATION_DB_PASSWORD="${NOTIFICATION_DB_PASSWORD:-notification_service}" \
  --dry-run=client -o yaml | kubectl apply -f -

log "Done. Restart the deployments to pick up changed values:"
log "  kubectl -n $NAMESPACE rollout restart deployment"
