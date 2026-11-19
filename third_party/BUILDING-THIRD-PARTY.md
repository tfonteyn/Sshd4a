# This is how the zstd+lz4 git modules were added:

git submodule add https://github.com/meta/zstd.git third_party/zstd
git -C third_party/zstd fetch --tags
git -C third_party/zstd checkout v1.5.7
git add .gitmodules third_party/zstd

git submodule add https://github.com/lz4/lz4.git third_party/lz4
git -C third_party/lz4 fetch --tags
git -C third_party/lz4 checkout v1.10.0
git add .gitmodules third_party/lz4

#  #############################################################################

# Define the physical locations.

#  #############################################################################

# The root directory of the Ssh4d project (i.e. where the projects .git is)

export PROJECT_ROOT=/c/Data/AndroidStudioProjects/Sshd4a

# Android SDK location

export ANDROID_HOME=/c/d/sdk

#  #############################################################################

# SDK and NDK versions - upgrade as needed/wanted

#  #############################################################################

# Android NDK version;

# This must match the version set in gradle/libs.versions.toml

export ANDROID_NDK=${ANDROID_HOME}/ndk/30.0.16248370

# Android minSdkVersion.

export ANDROID_MIN_SDK=30

#  #############################################################################

# Build platform - Only choose ONE of each, depending on your platform/device.

#  #############################################################################

# The platform running the build

export HOST_ARCH=windows-x86_64

# export HOST_ARCH=linux-x86_64

# The android architecture

# The first, "aarch64", is most likely the one you want.

export ANDROID_TARGET=aarch64-linux-android

# export ANDROID_TARGET=armv7a-linux-androideabi

# export ANDROID_TARGET=i686-linux-android

# export ANDROID_TARGET=x86_64-linux-android

#  #############################################################################

# Below this point, no configuration changes needed

#  #############################################################################

export TOOLCHAIN=${ANDROID_NDK}/toolchains/llvm/prebuilt/${HOST_ARCH}
export AR=${TOOLCHAIN}/bin/llvm-ar
export CC=${TOOLCHAIN}/bin/${ANDROID_TARGET}${ANDROID_MIN_SDK}-clang
export AS=${CC}
export CXX=${TOOLCHAIN}/bin/${ANDROID_TARGET}${ANDROID_MIN_SDK}-clang++
export LD=${TOOLCHAIN}/bin/ld
export RANLIB=${TOOLCHAIN}/bin/llvm-ranlib
export STRIP=${TOOLCHAIN}/bin/llvm-strip

# Used by Windows tar for symlinks.

export MSYS=winsymlinks:nativestrict

# add the tools to the path

export PATH=${ANDROID_HOME}/platform-tools:${TOOLCHAIN}/bin:${ANDROID_NDK}/prebuilt/$
{HOST_ARCH}/bin:${PATH}

# Destination for the builds/includes

export THIRD_PARTY_PREFIX="${PROJECT_ROOT}/build/third_party_prefix"
mkdir -p "${THIRD_PARTY_PREFIX}/include" "${THIRD_PARTY_PREFIX}/lib"

#  #############################################################################

# Build ZSTD

#  #############################################################################

export TP_ROOT="${PROJECT_ROOT}/third_party"

make -C "${TP_ROOT}/zstd/lib" clean OS=Linux
make -C "${TP_ROOT}/zstd/lib" libzstd.a OS=Linux

cp "${TP_ROOT}/zstd/lib/libzstd.a" "${THIRD_PARTY_PREFIX}/lib/"
cp "${TP_ROOT}/zstd/lib/zstd.h" "${THIRD_PARTY_PREFIX}/include/"
cp "${TP_ROOT}/zstd/lib/zstd_errors.h" "${THIRD_PARTY_PREFIX}/include/"

make -C "${TP_ROOT}/lz4/lib" clean OS=Linux
make -C "${TP_ROOT}/lz4/lib" liblz4.a OS=Linux

cp "${TP_ROOT}/lz4/lib/liblz4.a" "${THIRD_PARTY_PREFIX}/lib/"
cp "${TP_ROOT}/lz4/lib/lz4.h" "${TP_ROOT}/lz4/lib/lz4frame.h" "${TP_ROOT}/lz4/lib/lz4hc.h" "${THIRD_PARTY_PREFIX}/include/"
