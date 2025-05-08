![banner.jpg](demo/banner.jpg)
# 📚 StoryVoyage

StoryVoyage is an Android application designed to provide an immersive reading experience with features like AI assistance, voice recognition, and PDF viewing. Built using modern Android development tools such as Jetpack Compose, Koin for dependency injection, and Nutrient (Formally known as PSPDFKit) for PDF rendering.

## 🛠 Demo
Experience the app's features in action with a fully interactive walkthrough.

<table>
  <tr>
    <td><b>AI Assistant in Action</b></td>
    <td><b>Theme Overview</b></td>
    <td><b>PDF Viewer Showcase</b></td>
    <td><b>Voice Recognition Demo</b></td>

  </tr>
  <tr>
    <td>
      <video src="demo/ai-chat.mov" width="360" height="800" controls></video>
    </td>
    <td>
      <video src="demo/theme.mov" width="360" height="800" controls></video>
    </td>
    <td>
      <video src="demo/reader.mov" width="360" height="800" controls></video>
    </td>
    <td>
      <video src="demo/voice-recognition.mov" width="360" height="800" controls></video>
    </td>
  </tr>
</table>

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
- **PDF Rendering**: PSPDFKit
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

4. **Add Nutrient license key**:
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