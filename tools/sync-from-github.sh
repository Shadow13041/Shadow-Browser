#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git wurde noch nicht initialisiert. Siehe README.md." >&2
  exit 1
fi
if ! git diff --quiet || ! git diff --cached --quiet; then
  echo "Lokale Änderungen vorhanden. Erst committen oder sichern, dann erneut synchronisieren." >&2
  exit 1
fi
git pull --rebase origin main
./tools/verify.sh
