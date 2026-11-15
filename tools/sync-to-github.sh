#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git wurde noch nicht initialisiert. Siehe README.md." >&2
  exit 1
fi

./tools/verify.sh
git add -A
if ! git diff --cached --quiet; then
  git commit -m "Update Shadow Browser"
fi
git pull --rebase origin main
git push origin main
