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
    <td><img src="https://github.com/user-attachments/assets/98de3208-7bcc-41ba-8afe-3dd5278cfac3" alt="Register Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/b0063fb8-efee-4097-817b-bd6db3a22d42" alt="Login Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/cf62b3f2-13ca-42ca-9a9f-1a9dc488956e" alt="Main Feed Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/17d5dace-234e-4432-b916-1e5d27bda7bd" alt="Search Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/388dfc5d-2a05-417d-bc7c-2c446a56cce3" alt="Chat Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/72650b13-ea65-4366-80bd-eaff41d11672" alt="Message Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/7a87392c-1343-4e48-bbc7-9dd3820c8218" alt="Create Post Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/04484dcd-2cc5-46f4-92b0-081d1872c001" alt="Activity Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/087a3446-1f1b-493d-bfe6-cd145b525bb5" alt="Liked By Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/074bbefc-47db-4722-ae2f-4f228e7fa1c1" alt="Own Profile Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/e817fb3a-2226-4158-ab1e-074267f7e7df" alt="User Profile Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/c09373fa-e8db-4499-8ebb-74199a08b31a" alt="Followers Screen" width="400"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/685347b5-6130-4617-88fa-2a7806385531" alt="Post Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/790f8279-6cd7-4f08-af20-3b27ecb1b701" alt="Saved Post Screen" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/01c90e26-aeaf-4094-b686-3e6da2af737d" alt="Following Screen" width="400"/></td>
  </tr>
  <tr>
    <td colspan="3"><img src="https://github.com/user-attachments/assets/623b3a0b-21b1-46b8-8934-46d159eac81d" alt="Edit Profile Screen" width="400"/></td>
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
