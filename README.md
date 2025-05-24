# ChatApp (by aarchangel)

## Overview

ChatApp is a privacy-first, modern messaging application built with a focus on security and a clean user experience. This Android application is developed using the latest technologies, primarily Jetpack Compose for the UI and Kotlin as the programming language. The current user-facing name for the app during early development is "GhostTalk".

## Technologies Used

*   **Programming Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Build System:** Gradle
*   **Architecture:** (To be defined - currently following basic Android app structure with a focus on UI and config separation)

## Build Instructions

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd chatapp_project
    ```
2.  **Ensure you have Android Studio installed** (latest stable version recommended) or at least the Android SDK and a compatible JDK.
3.  **Set up your `JAVA_HOME` environment variable** if not already set.
4.  **Build the project using Gradle Wrapper:**
    ```bash
    ./gradlew build
    ```
5.  **Install on a connected device or emulator:**
    ```bash
    ./gradlew installDebug
    ```
    Alternatively, open the project in Android Studio and run it directly from the IDE.

## Project Structure

```
chatapp_project/
├── app/
│   ├── build.gradle.kts        # App-level Gradle build script
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Core Android manifest file
│           ├── java/
│           │   └── com/
│           │       └── aarchangel/
│           │           └── chatapp/
│           │               ├── MainActivity.kt       # Main entry point Activity
│           │               ├── config/
│           │               │   └── AppConfig.kt      # Global application configuration
│           │               ├── ui/
│           │               │   ├── screens/          # Composable screen functions
│           │               │   │   └── WelcomeScreen.kt
│           │               │   │   └── AuthOptionsScreen.kt (Planned)
│           │               │   ├── theme/            # Jetpack Compose theme files
│           │               │   │   ├── Color.kt
│           │               │   │   ├── Theme.kt
│           │               │   │   └── Type.kt
│           │               │   └── components/       # Reusable UI components (Planned)
│           │               ├── data/               # Data sources, models (Planned)
│           │               ├── domain/             # Business logic, use cases (Planned)
│           │               └── repository/         # Data repositories (Planned)
│           └── res/                    # Android resource files
│               ├── drawable/           # Drawable resources (icons, etc.)
│               ├── mipmap-*/           # Launcher icons (actual PNGs should go here)
│               ├── values/             # XML resource values (strings, colors, styles, themes)
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── xml/                # XML configuration (backup rules, etc.)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .gitignore                  # Specifies intentionally untracked files
├── build.gradle.kts            # Root-level Gradle build script
├── gradle.properties           # Project-wide Gradle settings
├── gradlew                     # Gradle wrapper script (Unix)
├── gradlew.bat                 # Gradle wrapper script (Windows)
├── LICENSE                     # Project license
├── README.md                   # This file
├── CODE_OF_CONDUCT.md          # Code of conduct for contributors
├── CONTRIBUTING.md             # Guidelines for contributing
└── settings.gradle.kts         # Gradle settings script
```

## Future Roadmap (Suggestions)

*   **Full Authentication:** Implement robust authentication for Email/Password, Phone, Gmail, and Apple sign-ins (e.g., using Firebase Authentication, Supabase Auth, or a custom backend).
*   **Dark/Light Theme Toggle:** Allow users to manually switch between dark and light themes, respecting system settings as a default.
*   **Splash Screen:** Add an engaging splash screen with an animation.
*   **End-to-End Encrypted (E2EE) Messaging:** Implement secure one-on-one and group chat functionality with E2EE.
*   **User Profiles & Settings:** Allow users to manage their profiles and application settings.
*   **Contact List & Management:** Integrate with device contacts and allow for in-app contact management.
*   **Push Notifications:** Implement real-time notifications for new messages and other important events.
*   **Accessibility Features:** Enhance accessibility with good support for TalkBack, adjustable font sizes, and high contrast modes.
*   **Offline Support:** Cache data to provide a seamless experience even when the user is offline.
*   **Testing:** Implement comprehensive unit, integration, and UI tests. 