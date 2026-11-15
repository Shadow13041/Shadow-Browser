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
  test -f "$file"
  echo "OK: $file"
done

grep -q "showTabOverview" app/src/main/java/de/shadow13041/shadowbrowser/MainActivity.java
grep -q "configureWebView" app/src/main/java/de/shadow13041/shadowbrowser/MainActivity.java

git diff --check
echo "Projektstruktur-Prüfung erfolgreich."
echo "Hinweis: Dies ersetzt keinen Android-Gradle-Build."
