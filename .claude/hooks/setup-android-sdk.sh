#!/bin/bash
# Installs the Android SDK command-line tools and the packages this project
# needs (platform-tools, platform 36, build-tools 36.0.0) so `./gradlew
# assembleDebug` etc. work in Claude Code cloud sessions.
#
# Requires the environment's network access to allow:
#   - dl.google.com    (Android SDK packages)
#   - maven.google.com (AndroidX/Compose Maven artifacts via the `google()` repo)
set -euo pipefail

if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-/opt/android-sdk}"
CMDLINE_TOOLS_BUILD="14742923"
SDKMANAGER="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

if [ ! -x "$SDKMANAGER" ]; then
  mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
  tmp_zip="$(mktemp)"
  curl -fsSL -o "$tmp_zip" "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_BUILD}_latest.zip"
  unzip -q -o "$tmp_zip" -d "$ANDROID_SDK_ROOT/cmdline-tools"
  rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  rm -f "$tmp_zip"
fi

export ANDROID_SDK_ROOT
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"

yes | sdkmanager --licenses >/dev/null 2>&1 || true
yes | sdkmanager --install "platform-tools" "platforms;android-36" "build-tools;36.0.0" >/dev/null

echo "sdk.dir=$ANDROID_SDK_ROOT" > "$CLAUDE_PROJECT_DIR/local.properties"

{
  echo "export ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
  echo "export ANDROID_HOME=$ANDROID_SDK_ROOT"
  echo "export PATH=\"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:\$PATH\""
} >> "$CLAUDE_ENV_FILE"
