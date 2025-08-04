package com.connectify.android.core.util

sealed class Screen(val route: String) {
    data object AuthScreen : Screen("auth_screen")
    data object OnBoardingScreen : Screen("on_boarding_screen")
    data object LoginScreen : Screen("login_screen")
    data object RegisterScreen : Screen("register_screen")
    data object OtpScreen : Screen("otp_screen")
    data object PasswordScreen : Screen("password_screen")
    data object MainFeedScreen : Screen("main_feed_screen")
    data object PostDetailScreen : Screen("post_detail_screen")
    data object ChatScreen : Screen("chat_screen")
    data object MessageScreen : Screen("message_screen")
    data object ProfileScreen : Screen("profile_screen")
    data object EditProfileScreen : Screen("edit_profile_screen")
    data object PersonListScreen : Screen("person_list_screen")
    data object FollowingScreen : Screen("following_screen")
    data object FollowerScreen : Screen("follower_screen")
    data object CreatePostScreen : Screen("create_post_screen")
    data object ActivityScreen : Screen("activity_screen")
    data object SearchScreen : Screen("search_screen")
    data object SavedPostScreen : Screen("saved_post_screen")
    data object CropScreen : Screen("crop_screen")
}