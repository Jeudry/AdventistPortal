#!/usr/bin/env bash
#
# gen-jwt-keys.sh — an RSA key pair for signing access tokens.
#
# The private half goes to the user service alone; everyone else gets the public half and
# can only verify. Prints both base64-encoded on one line each, ready for .env.
#
# Usage: scripts/gen-jwt-keys.sh
#
set -euo pipefail

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$tmp/private.pem" 2>/dev/null
openssl rsa -in "$tmp/private.pem" -pubout -out "$tmp/public.pem" 2>/dev/null

strip() { grep -v -- '-----' "$1" | tr -d '\n'; }

echo "JWT_PRIVATE_KEY=$(strip "$tmp/private.pem")"
echo "JWT_PUBLIC_KEY=$(strip "$tmp/public.pem")"
