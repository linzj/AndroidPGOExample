# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(PGO_PHASE 2)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("pgoexample")

# CMake configuration for enabling PGO instrumentation and optimization

# Set default PGO phase to 0 (no PGO)
if (NOT DEFINED PGO_PHASE)
    set(PGO_PHASE 2)
endif()

# Set default PGO data path to an empty string
if (NOT DEFINED PGO_DATA_PATH)
    set(PGO_DATA_PATH "")
endif()

# Detect the target platform and architecture for PGO profile data
if (CMAKE_SYSTEM_NAME STREQUAL "Windows")
    if (CMAKE_SYSTEM_PROCESSOR STREQUAL "AMD64")
        set(PGO_TARGET "win64")
    elseif (CMAKE_SYSTEM_PROCESSOR STREQUAL "ARM64")
        set(PGO_TARGET "win-arm64")
    else()
        set(PGO_TARGET "win32")
    endif()
elseif (CMAKE_SYSTEM_NAME STREQUAL "Darwin")
    if (CMAKE_SYSTEM_PROCESSOR STREQUAL "ARM64")
        set(PGO_TARGET "mac-arm")
    else()
        set(PGO_TARGET "mac")
    endif()
elseif (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    set(PGO_TARGET "linux")
elseif (CMAKE_SYSTEM_NAME STREQUAL "Android")
    if (CMAKE_SYSTEM_PROCESSOR STREQUAL "aarch64")
        set(PGO_TARGET "android-arm64")
    else()
        set(PGO_TARGET "android-arm32")
    endif()
else()
    message(FATAL_ERROR "Unsupported platform for PGO")
endif()

# Function to setup PGO instrumentation flags
function(enable_pgo_instrumentation target)
    if (PGO_PHASE EQUAL 1)
        # Add the PGO_GEN_PHASE definition
        target_compile_definitions(${target} PRIVATE PGO_GEN_PHASE=1)

        target_compile_options(${target} PRIVATE -fprofile-generate)
        if (NOT CMAKE_SYSTEM_NAME STREQUAL "Windows")
            target_link_options(${target} PRIVATE -fprofile-generate)
        endif()
    endif()
endfunction()

# Function to setup PGO optimization flags
function(enable_pgo_optimization target)
    if (PGO_PHASE EQUAL 2)
        if (PGO_DATA_PATH STREQUAL "")
            # Set the default profile path based on the PGO target
            set(PGO_DATA_PATH "${CMAKE_SOURCE_DIR}/profiles/${PGO_TARGET}.pgo")
        endif()
        
        # Check if the profile path is valid
        if (NOT EXISTS ${PGO_DATA_PATH})
            message(FATAL_ERROR "PGO data file not found: ${PGO_DATA_PATH}")
        endif()

        target_compile_options(${target} PRIVATE 
            -fprofile-use=${PGO_DATA_PATH}
            -Wno-profile-instr-unprofiled
            -Wno-profile-instr-out-of-date
            -Wno-backend-plugin
        )
        
        if (USE_THIN_LTO)
            if (CMAKE_SYSTEM_NAME STREQUAL "Windows")
                target_link_options(${target} PRIVATE -mllvm:-enable-ext-tsp-block-placement=1)
            else()
                target_link_options(${target} PRIVATE -Wl,-mllvm,-enable-ext-tsp-block-placement=1)
            endif()
        else()
            target_compile_options(${target} PRIVATE -mllvm -enable-ext-tsp-block-placement=1)
        endif()
    endif()
endfunction()

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)

# Enable PGO instrumentation or optimization based on the phase
if (PGO_PHASE EQUAL 1)
    enable_pgo_instrumentation(${CMAKE_PROJECT_NAME})
elseif (PGO_PHASE EQUAL 2)
    enable_pgo_optimization(${CMAKE_PROJECT_NAME})
endif()