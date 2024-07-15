package com.example.connectify.core.util

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.connectify.core.domain.models.Post
import com.example.connectify.feature_activity.presentation.activity.ActivityScreen
import com.example.connectify.feature_chat.presentation.chat.ChatScreen
import com.example.connectify.feature_post.presentation.create_post.CreatePostScreen
import com.example.connectify.feature_profile.presentation.edit_profile.EditProfileScreen
import com.example.connectify.feature_auth.presentation.login.LoginScreen
import com.example.connectify.feature_post.presentation.main_feed.MainFeedScreen
import com.example.connectify.feature_post.presentation.post_detail.PersonListScreen
import com.example.connectify.feature_post.presentation.post_detail.PostDetailScreen
import com.example.connectify.feature_profile.presentation.profile.ProfileScreen
import com.example.connectify.feature_auth.presentation.register.RegisterScreen
import com.example.connectify.feature_profile.presentation.search.SearchScreen
import com.example.connectify.feature_auth.presentation.splash.SplashScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(
                onPopBackStack = navController::popBackStack,
                onNavigate = navController::navigate
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                onNavigate = navController::navigate,
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                navController = navController,
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.MainFeedScreen.route) {
            MainFeedScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.ChatScreen.route) {
            ChatScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
            )
        }
        composable(Screen.ActivityScreen.route) {
            ActivityScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
            )
        }
        composable(
            route = Screen.ProfileScreen.route + "?userId={userId}",
            arguments = listOf(
                navArgument(name = "userId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ProfileScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.CreatePostScreen.route) {
            CreatePostScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.EditProfileScreen.route) {
            EditProfileScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
            )
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
            )
        }
        composable(Screen.PersonListScreen.route) {
            PersonListScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
            )
        }
        composable(Screen.PostDetailScreen.route) {
            PostDetailScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                post = Post(
                    username = "mauli.waghmore",
                    imageUrl = "",
                    profilePictureUrl = "",
                    description = "Are you ready to take control of your financial future? Discover proven strategies to grow your wealth, manage your expenses, and invest wisely. Whether you're just starting out or looking to optimize your current financial plan, our expert tips and advice will help you unlock your financial potential. From budgeting techniques to investment insights, we've got everything you need to make your money work for you. Start your journey towards financial freedom today!",
                    likeCount = 70,
                    commentCount = 19
                )
            )
        }
    }
}