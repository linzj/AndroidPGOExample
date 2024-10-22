# AndroidPGOExample

This is a minimal example demonstrating how to use Profile-Guided Optimization (PGO) in an Android application. PGO helps optimize the performance of your application by collecting runtime profiling data during a training phase and then using that data to optimize the final build.

## Table of Contents

- [AndroidPGOExample](#androidpgoexample)
  - [Table of Contents](#table-of-contents)
  - [Prerequisites](#prerequisites)
  - [Setup](#setup)
  - [Enabling PGO](#enabling-pgo)
    - [Step 1: Instrumentation Phase (PGO\_PHASE=1)](#step-1-instrumentation-phase-pgo_phase1)
    - [Step 2: Optimization Phase (PGO\_PHASE=2)](#step-2-optimization-phase-pgo_phase2)
  - [Building the Project](#building-the-project)
  - [Contributing](#contributing)
  - [License](#license)

## Prerequisites

Before you start, ensure you have the following installed:

- **Android Studio**: The latest version is recommended.
- **NDK**: Ensure that the NDK is installed and configured in your Android Studio.
- **CMake**: Version 3.22.1 or later.

## Setup

1. **Clone the Repository**:

   ```sh
   git clone https://github.com/yourusername/AndroidPGOExample.git
   cd AndroidPGOExample
   ```

2. **Open the Project in Android Studio**:
   - Open Android Studio and select "Open an existing Android Studio project".
   - Navigate to the cloned repository and open it.

## Enabling PGO

### Step 1: Instrumentation Phase (PGO_PHASE=1)

1. **Set PGO_PHASE to 1**:
   - In `CMakeLists.txt`, set `set(PGO_PHASE 1)`.

2. **Build and Run the Application**:
   - Build and run the application on a device or emulator.
   - The application will collect profiling data during this phase.

3. **Running the Application**

   1. **Start Profiling**:
   - Call the `startProfiling` method from your Java code to begin the profiling process.
   - Example:

     ```java
     String result = startProfiling("path/to/profile_file.profdata");
     Log.d("MainActivity", result);
     ```

   1. **Stop Profiling**:
   - Call the `stopProfiling` method to stop the profiling process.
   - Example:

     ```java
     stopProfiling();
     ```

4. **Merge PGORawData to pgo data**:
   - Example command: `${ANDROID_SDK}/Sdk/ndk/28.0.12433566/toolchains/llvm/prebuilt/windows-x86_64/bin/llvm-profdata.exe merge -o app/src/main/cpp/profiles/android-arm64.pgo profile.pgo`.

### Step 2: Optimization Phase (PGO_PHASE=2)

1. **Set PGO_PHASE to 2**:
   - In `CMakeLists.txt`, set `set(PGO_PHASE 2)`.
   - Ensure that the profiling data file exists in the specified path. By default, it should be located at `profiles/${PGO_TARGET}.pgo`.

2. **Build and Run the Application**:
   - Build and run the application again.
   - The application will now use the collected profiling data to optimize the build.

## Building the Project

1. **Open Terminal**:
   - Open the terminal in Android Studio.

2. **Navigate to the Project Directory**:

   ```sh
   cd path/to/AndroidPGOExample
   ```

3. **Build the Project**:

   ```sh
   ./gradlew assembleDebug
   ```

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.