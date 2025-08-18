# Connectify

[👉 Download on Google Play](https://play.google.com/store/apps/details?id=com.connectify.android)

Connectify is a modern social network application. It offers a seamless user experience for connecting, sharing, and discovering content. Built using cutting-edge technologies like Kotlin, Jetpack Compose, Ktor for the backend, and MongoDB as the database, Connectify is designed for performance, scalability, and user engagement.

## Features

- **User Authentication**: Secure sign-up and login functionality using JWT.
- **Feed**: View posts from followed users in a feed.
- **Search**: Explore content and search for other users.
- **Create Post**: Users can upload images and captions.
- **Save Posts**: Users can save posts and view them in a dedicated section.
- **Like and Comment**: Interact with posts by liking or commenting on them.
- **Notifications**: Stay updated with alerts about activities like likes, comments, and follows.
- **Deep Linking**: Navigate directly to specific profiles via notifications.
- **Responsive UI**: Built using Jetpack Compose for modern UI and a responsive user experience.

## Tech Stack

### Android
- **Kotlin**: The primary programming language used for developing the Android application.
- **Jetpack Compose**: UI toolkit for modern native Android interfaces, allowing for declarative UI programming.
- **Hilt**: Dependency Injection library that simplifies the process of managing dependencies and enhances testability.
- **Coil**: An image loading library for Android, optimized for Kotlin and designed for handling image loading efficiently.

### Backend
- **Ktor**: Asynchronous Kotlin framework for the server.
- **MongoDB**: NoSQL database to store user data and posts.

### Architecture
<div>
  <pre>
    <code>
      com.connectify.android
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
