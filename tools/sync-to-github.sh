#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

git rev-parse --is-inside-work-tree >/dev/null
./tools/verify.sh

git add -A
if ! git diff --cached --quiet; then
  git commit -m "Update Shadow Browser"
fi

git pull --rebase origin main
git push origin main

echo "GitHub-Synchronisierung erfolgreich."
