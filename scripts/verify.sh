#!/usr/bin/env bash
# The full local verification for this repository: assemble the app and run the unit tests.
# This is what CI runs, so a green run here means a green run there.
set -uo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR" || exit 1

if [ ! -x ./gradlew ]; then
  echo "./gradlew is missing or not executable — regenerate it with: gradle wrapper" >&2
  exit 1
fi

echo "==> assembleDebug"
./gradlew :app:assembleDebug || exit 1

echo "==> testDebugUnitTest"
./gradlew :app:testDebugUnitTest || exit 1

# Gradle reports BUILD SUCCESSFUL when a test task runs zero tests, so the exit code above
# does not answer the question this script exists to answer. Read the report instead.
echo "==> Confirming tests actually ran"
RESULTS_DIR="app/build/test-results/testDebugUnitTest"
TOTAL=$(grep -ho 'tests="[0-9]*"' "$RESULTS_DIR"/*.xml 2>/dev/null \
  | sed 's/[^0-9]//g' | awk '{ sum += $1 } END { print sum + 0 }')

if [ -z "$TOTAL" ] || [ "$TOTAL" -eq 0 ]; then
  echo "  no test results in $RESULTS_DIR — the task reported success without running anything" >&2
  exit 1
fi
echo "  $TOTAL tests ran"

echo
echo "Verified: app assembles and $TOTAL unit tests pass."
