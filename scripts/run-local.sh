#!/usr/bin/env sh
set -eu

if [ -z "${APP_USERNAME:-}" ]; then
  echo "APP_USERNAME is required." >&2
  exit 1
fi

if [ -z "${APP_PASSWORD:-}" ]; then
  echo "APP_PASSWORD is required." >&2
  exit 1
fi

./mvnw test -Denv=local -Dservice=platform -Dapp.username="$APP_USERNAME" -Dapp.password="$APP_PASSWORD" "$@"
