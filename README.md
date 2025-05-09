![banner.jpg](demo/banner.jpg)
# 📚 StoryVoyage

StoryVoyage is an Android application designed to provide an immersive reading experience with features like AI assistance, voice recognition, and PDF viewing. Built using modern Android development tools such as Jetpack Compose, Koin for dependency injection, and Nutrient (Formally known as PSPDFKit) for PDF rendering.

## 🛠 Demo
Experience the app's features in action with a fully interactive walkthrough.

| AI Assistant in Action | Theme Overview | PDF Viewer Showcase | Voice Recognition Demo |
| --- | --- | --- | --- |
| <video src="https://github.com/user-attachments/assets/3e377f13-ba76-4cb6-b253-c4208619b25a"  controls="controls"></video> | <video src="https://github.com/user-attachments/assets/d8789d1a-3ec8-4e29-b65e-6f78c1020b74"  controls="controls"></video> | <video src="https://github.com/user-attachments/assets/ab5c62d1-acb4-4b0a-b6d4-23647019a7b4"  controls="controls"></video> | <video src="https://github.com/user-attachments/assets/e5066a57-4723-4008-a376-1d16a86c796e"  controls="controls"></video> |

## 🚀 Features

- **AI Assistant**: Interact with an AI assistant for book recommendations and queries.
- **Voice Recognition**: Use voice commands to navigate and interact with the app.
- **PDF Viewer**: Seamlessly view and interact with PDF documents.
- **Custom Themes**: Switch between light, dark, and auto themes based on user preferences.
- **Edge-to-Edge Design**: Modern UI with edge-to-edge support for immersive experiences.

## 🛠 Tech Stack

- **Programming Language**: Kotlin
- **Frameworks**: Jetpack Compose, Koin, PSPDFKit
- **Dependency Injection**: Koin
- **PDF Rendering**: Nutrient (PSPDFKit)
- **Build Tool**: Gradle

## 📂 Project Structure

- `app/src/main/java/io/ak1/demo/`: Contains the main application code.
- `app/src/main/assets/`: Includes assets like keys and licenses.
- `app/src/main/res/`: Contains UI resources such as strings.

## 🛠 Setup Instructions

### Prerequisites

- Android Studio (latest version recommended)
- Docker installed on your system

### Steps to Run the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/akshay2211/StoryVoyage.git
   cd StoryVoyage
   ```

2. **Open the project in Android Studio**:
    - Launch Android Studio.
    - Click on "Open" and select the `StoryVoyage` project directory.

3. **Sync Gradle files**:
    - Android Studio will automatically detect the `build.gradle` files and sync the project.
    - If not, click on "File" > "Sync Project with Gradle Files".

4. **Add RSA key**:
    - Place your RSA private key in the `app/src/main/assets/keys/jwt.pem` file.
    - A jwt.pem file contains an RSA private key in PEM (Privacy Enhanced Mail) format that's used for signing JSON Web Tokens (JWTs).

5. **Run the Nutrient AI Assistant server**:
   ```bash
   git clone https://github.com/PSPDFKit/ai-assistant-demo
   cd ai-assistant-demo
   docker-compose up
   ```
    - Ensure the server is running before launching the app.

6. **Run the app**:
    - Connect an Android device or start an emulator.
    - Click on the "Run" button in Android Studio or use the shortcut `Shift + F10`.

## 📋 Dependencies

- **Jetpack Compose**: Modern UI toolkit for building native Android apps.
- **Koin**: Dependency injection framework.
- **Nutrient**: SDK for PDF rendering and interaction.
- **Lottie**: For animations.

## 📘 Resources and Licenses

### Libraries & SDKs

| Component       | License Type | Source URL                                    |
|-----------------|--------------|-----------------------------------------------|
| Jetpack Compose | Apache 2.0   | https://developer.android.com/jetpack/compose |
| Koin            | Apache 2.0   | https://insert-koin.io                        |
| Nutrient        | Proprietary  | https://nutrient.io                          |
| Lottie          | Apache 2.0   | https://airbnb.io/lottie                      |

### Public Domain Books

The app includes public domain books sourced from [InfoBooks.org](https://www.infobooks.org).

### Icons 
Icon <a href="https://www.flaticon.com/free-icons/reading" title="reading icons">created by mangsaabguru - Flaticon</a>

## 📬 Contact

For questions, issues, or feature requests, please open an issue in the repository.

## 🛡 Disclaimer

This project is for educational purposes. Ensure compliance with all licenses and terms of use for third-party libraries and resources.
