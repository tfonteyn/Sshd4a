#!/bin/bash

# 2026-05-16: first experiment with an automated build script, use with care

if [ -z "$ANDROID_HOME" ] || [ ! -d "$ANDROID_HOME" ]; then
    echo >&2 "Error: ANDROID_HOME is not set or the directory does not exist."
    echo >&2 "Please set it first (e.g., export ANDROID_HOME=\$HOME/android-sdk)"
    exit 1
fi

if ! command -v sdkmanager >/dev/null 2>&1; then
    echo >&2 "Error: 'sdkmanager' not found."
    echo >&2 "Please add your Android SDK 'cmdline-tools/bin' or 'tools/bin' directory to the PATH."
    exit 1
fi

if [ ! -d "app" ]; then
    echo >&2 "Error: 'app' directory not found."
    echo >&2 "Please execute the build from within the project root directory."
    exit 1
fi

# Tell Gradle where the sdk is
if [ ! -f local.properties ]; then
    echo "sdk.dir=$ANDROID_HOME" > local.properties
fi

# ############################################################################
# CMake setup
# ############################################################################
# Extract the cmake version needed
export CMAKE_VER=$(awk -F'"' '/^cmakeVersion[[:space:]]*=/ {print $2}' gradle/libs.versions.toml)
if [ -z "$CMAKE_VER" ]; then
    echo >&2 "Error: Could not parse cmakeVersion from libs.versions.toml"
    exit 1
fi

# cmake must be installed manually by sdkmanager
# TODO: Gradle seems to refuse installing it if there is a global cmake command?
# Will do nothing if it's already there.
# TODO: sdkmanager is deprecated; use "android sdk install" command from newer tools version
sdkmanager "cmake;$CMAKE_VER"

# Add to the path
export PATH=$PATH:$ANDROID_HOME/cmake/$CMAKE_VER/bin

# ############################################################################
# Sshd4a build
# ############################################################################

# third-party gits
git submodule update --init --recursive

# run the prebuild scripts
app/src/main/cpp/prebuild-rsyn.sh
app/src/main/cpp/prebuild-dropbear.sh

# and finally build
./gradlew assembleRelease