#!/usr/bin/env bash
set -euo pipefail
project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
python3 "$project_dir/scripts/prepare-runtime.py"
cd "$project_dir/build/java-runtime"
chmod +x gradlew
./gradlew jar
printf '%s\n' "JavaRuntime: $project_dir/build/java-runtime/build/libs/JavaRuntime.jar" "Theme: $project_dir/build/runtime"
