# Connectify

Connectify is a social media application inspired by Instagram, designed to let users share posts, explore content, and connect with others. This app is built using **Kotlin**, **Jetpack Compose**, and **Ktor** backend, with **MongoDB** as the database.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Architecture](#architecture)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

## Features

- **User Authentication**: Secure sign-up and login functionality using JWT.
- **Feed**: View posts from followed users in a feed.
- **Search**: Explore content and search for other users.
- **Create Post**: Users can upload images and captions.
- **Save Posts**: Users can save posts and view them in a dedicated section.
- **Like and Comment**: Interact with posts by liking or commenting on them.
- **Responsive UI**: Built using Jetpack Compose for modern UI and a responsive user experience.

## Tech Stack

### Frontend
- **Kotlin**
- **Jetpack Compose**: UI toolkit for modern native Android interfaces.
- **Kotlin Coroutines**: For asynchronous programming.
- **Paging 3**: For loading data in pages.
- **Hilt**: Dependency Injection.
- **Coil**: For image loading.

### Backend
- **Ktor**: Asynchronous Kotlin framework for the server.
- **MongoDB**: NoSQL database to store user data and posts.
- **JWT**: For secure user authentication.
- **Kotlin Serialization**: For JSON parsing.

### Architecture
com.example.connectify
├── core            # Contains shared code such as utilities, constants, or base classes
│   └── util
├── data            # Contains repository implementations and data source classes
│   └── repository
│   └── remote      # Network layer
│   └── local       # Local data handling (e.g., Room or MongoDB)
├── domain          # Domain logic and business rules
│   └── models      # Core business models (Post, User)
│   └── usecases    # Use cases like saving a post, getting posts, etc.
├── presentation    # UI-related code
│   └── ui
│       └── components # Composable components like Snackbar, PostCard, etc.
│       └── screens    # Individual screens (MainFeed, Search, etc.)
│   └── viewmodel      # ViewModels for the screens

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

 
