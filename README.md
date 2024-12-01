# Connectify

Connectify is a social media application inspired by Instagram, designed to let users share posts, explore content, and connect with others. This app is built using **Kotlin**, **Jetpack Compose**, and **Ktor** backend, with **MongoDB** as the database.

## Features

- **User Authentication**: Secure sign-up and login functionality using JWT.
- **Feed**: View posts from followed users in a feed.
- **Search**: Explore content and search for other users.
- **Create Post**: Users can upload images and captions.
- **Save Posts**: Users can save posts and view them in a dedicated section.
- **Like and Comment**: Interact with posts by liking or commenting on them.
- **Responsive UI**: Built using Jetpack Compose for modern UI and a responsive user experience.

## Tech Stack

### Android
- **Kotlin**: The primary programming language used for developing the Android application.
- **Jetpack Compose**: UI toolkit for modern native Android interfaces, allowing for declarative UI programming.
- **Kotlin Coroutines**: For asynchronous programming, enabling smooth and responsive UI by handling background tasks efficiently.
- **Paging**: For loading data in pages, improving performance and user experience when displaying large data sets.
- **Hilt**: Dependency Injection library that simplifies the process of managing dependencies and enhances testability.
- **Coil**: An image loading library for Android, optimized for Kotlin and designed for handling image loading efficiently.

### Backend
- **Ktor**: Asynchronous Kotlin framework for the server.
- **MongoDB**: NoSQL database to store user data and posts.
- **JWT**: For secure user authentication.
- **Kotlin Serialization**: For JSON parsing.

### Architecture
<div>
  <pre>
    <code>
      com.example.connectify
      ├── core            # Contains shared code such as utilities, constants, or base classes
      │   └── util
      ├── data            # Contains repository implementations and data source classes
      │   ├── repository
      │   ├── remote      # Network layer
      │   └── local       # Local data handling (e.g., Room or MongoDB)
      ├── domain          # Domain logic and business rules
      │   ├── models      # Core business models (Post, User)
      │   └── usecases    # Use cases like saving a post, getting posts, etc.
      ├── presentation     # UI-related code
      │   ├── ui
      │   │   ├── components # Composable components like Snackbar, PostCard, etc.
      │   │   └── screens    # Individual screens (MainFeed, Search, etc.)
      │   └── viewmodel      # ViewModels for the screens
    </code>
  </pre>
</div>

## Screenshots
<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Register_Screen.png" alt="Register Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Login_Screen.png" alt="Login Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Main_Feed_Screen.png" alt="Main Feed Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Search_Screen.png" alt="Search Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Chat_Screen.png" alt="Chat Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Message_Screen.png" alt="Message Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Create_Post_Screen.png" alt="Create Post Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Activity_Screen.png" alt="Activity Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Liked_By_Screen.png" alt="Liked By Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Own_Profile_Screen.png" alt="Own Profile Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/User_Profile_Screen.png" alt="User Profile Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Follwers_Screen.png" alt="Followers Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Post_Screen.png" alt="Post Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Saved_Post_Screen.png" alt="Saved Post Screen" width="400"/></td>
    <td><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Following_Screen.png" alt="Following Screen" width="400"/></td>
  </tr>
  <tr>
    <td colspan="3"><img src="https://raw.githubusercontent.com/Mauli96/Connectify/refs/heads/main/app/src/main/java/com/example/connectify/assets/screenshots/Edit_Profile_Screen.png" alt="Edit Profile Screen" width="400"/></td>
  </tr>
</table>

## Installation

### Prerequisites

- **Android Studio** (latest stable version)
- **MongoDB** installed and running locally or in the cloud.
- **Ktor** backend project (refer to the backend repository if it is separated).

### Steps

1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/connectify.git
    cd connectify
    ```

2. **Open with Android Studio**:
   Open the project in Android Studio and allow the IDE to sync and build.

3. **Setup Environment Variables**:
   Create a `local.properties` file for API keys, database credentials, and other environment-specific variables.

4. **Run the Application**:
   Click the **Run** button in Android Studio or use the following command:
   ```bash
   ./gradlew assembleDebug
