#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

required=(
  settings.gradle
  build.gradle
  gradle.properties
  app/build.gradle
  app/src/main/AndroidManifest.xml
  app/src/main/java/de/shadow13041/shadowbrowser/MainActivity.java
  app/src/main/res/values/styles.xml
)
for file in "${required[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "FEHLER: Datei fehlt: $file" >&2
    exit 1
  fi
done

if command -v python3 >/dev/null 2>&1; then
  python3 - <<'PY'
import xml.etree.ElementTree as ET
for path in ["app/src/main/AndroidManifest.xml", "app/src/main/res/values/styles.xml"]:
    ET.parse(path)
    print(f"XML OK: {path}")
PY
fi

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  git diff --check
fi

echo "Projektstruktur-Prüfung erfolgreich."
echo "Hinweis: Dies ersetzt keinen Android-Gradle-Build."
