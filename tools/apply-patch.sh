#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

test "$#" -eq 1
test -f "$1"

PATCH_FILE="$1"
git apply --check "$PATCH_FILE"
git apply "$PATCH_FILE"
./tools/verify.sh

echo "Patch angewendet und Basisprüfungen bestanden."
