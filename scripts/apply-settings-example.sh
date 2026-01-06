#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT=$(cd "$(dirname "$0")/.." && pwd)
TARGET_DIR="$HOME/.m2"
TARGET_FILE="$TARGET_DIR/settings.xml"

mkdir -p "$TARGET_DIR"
cp -f "$REPO_ROOT/settings-example.xml" "$TARGET_FILE"

echo "Copied settings-example.xml to $TARGET_FILE"
echo "Please edit that file to set your proxy, mirror URL, and any credentials required by your artifact repository."
