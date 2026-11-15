#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ $# -ne 1 || ! -f "$1" ]]; then
  echo "Verwendung: $0 /pfad/zum/patch.diff" >&2
  exit 2
fi

PATCH_FILE="$1"
if ! git apply --check "$PATCH_FILE"; then
  echo "PATCH ABGEBROCHEN: Der Patch passt nicht zum aktuellen Projektstand." >&2
  exit 1
fi

git apply "$PATCH_FILE"
if ! ./tools/verify.sh; then
  echo "Prüfung nach Patch fehlgeschlagen. Änderungen bleiben zur Fehleranalyse erhalten." >&2
  exit 1
fi

echo "Patch angewendet und Basisprüfungen bestanden."
