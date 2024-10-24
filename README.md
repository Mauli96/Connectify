![MainFeed_Screen](https://github.com/user-attachments/assets/96d5877b-5ff9-41b2-9087-94d41543e89d)# Connectify

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

## Screenshots

## Register Screen
![Register_Screen](https://github.com/user-attachments/assets/98de3208-7bcc-41ba-8afe-3dd5278cfac3)

## Login Screen
![Login_Screen](https://github.com/user-attachments/assets/b0063fb8-efee-4097-817b-bd6db3a22d42)

### Home Screen
![MainFeed_Screen](https://github.com/user-attachments/assets/cf62b3f2-13ca-42ca-9a9f-1a9dc488956e)

### Search Screen
![Search_Screen](https://github.com/user-attachments/assets/17d5dace-234e-4432-b916-1e5d27bda7bd)

### Chat Screen
![Chat_Screen](https://github.com/user-attachments/assets/388dfc5d-2a05-417d-bc7c-2c446a56cce3)

## Message Screen
![Message_Screen](https://github.com/user-attachments/assets/72650b13-ea65-4366-80bd-eaff41d11672)

## Create Post Screen
![Create_Post_Screen](https://github.com/user-attachments/assets/7a87392c-1343-4e48-bbc7-9dd3820c8218)

## Activity Screen
![Activity_Screen](https://github.com/user-attachments/assets/04484dcd-2cc5-46f4-92b0-081d1872c001)

## LikeBy Screen
![LikeBy_Screen](https://github.com/user-attachments/assets/087a3446-1f1b-493d-bfe6-cd145b525bb5)

## Own Profile Screen
![Own_Profile_Screen](https://github.com/user-attachments/assets/074bbefc-47db-4722-ae2f-4f228e7fa1c1)

## User Profile Screen
![User_Profile_Screen](https://github.com/user-attachments/assets/e817fb3a-2226-4158-ab1e-074267f7e7df)

## Followers Screen
![Followers_Screen](https://github.com/user-attachments/assets/c09373fa-e8db-4499-8ebb-74199a08b31a)

## Following Screen
![Following_Screen](https://github.com/user-attachments/assets/01c90e26-aeaf-4094-b686-3e6da2af737d)

## Saved Post Screen
![Saved_Post_Screen](https://github.com/user-attachments/assets/790f8279-6cd7-4f08-af20-3b27ecb1b701)

## Post Screen
![Post_Screen](https://github.com/user-attachments/assets/685347b5-6130-4617-88fa-2a7806385531)

## Edit Profile Screen
![Edit_Profile_Screen](https://github.com/user-attachments/assets/623b3a0b-21b1-46b8-8934-46d159eac81d)


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

 
