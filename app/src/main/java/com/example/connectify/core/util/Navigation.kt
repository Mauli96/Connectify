package com.example.connectify.core.util

import android.content.Intent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import coil.ImageLoader
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.connectify.core.domain.models.Post
import com.example.connectify.feature_activity.presentation.ActivityScreen
import com.example.connectify.feature_chat.presentation.chat.ChatScreen
import com.example.connectify.feature_post.presentation.create_post.CreatePostScreen
import com.example.connectify.feature_profile.presentation.edit_profile.EditProfileScreen
import com.example.connectify.feature_auth.presentation.login.LoginScreen
import com.example.connectify.feature_post.presentation.main_feed.MainFeedScreen
import com.example.connectify.feature_post.presentation.person_list.PersonListScreen
import com.example.connectify.feature_post.presentation.post_detail.PostDetailScreen
import com.example.connectify.feature_profile.presentation.profile.ProfileScreen
import com.example.connectify.feature_auth.presentation.register.RegisterScreen
import com.example.connectify.feature_profile.presentation.search.SearchScreen
import com.example.connectify.feature_auth.presentation.splash.SplashScreen
import com.example.connectify.feature_chat.presentation.message.MessageScreen
import com.example.connectify.feature_profile.presentation.follower.FollowerScreen
import com.example.connectify.feature_profile.presentation.following.FollowingScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    imageLoader: ImageLoader
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
                onLogin = {
                    navController.popBackStack(
                        route = Screen.LoginScreen.route,
                        inclusive = true
                    ) ||  navController.popBackStack(
                        route = Screen.RegisterScreen.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.MainFeedScreen.route)
                },
                scaffoldState = scaffoldState
            )
        }
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                onNavigate = navController::navigate,
                scaffoldState = scaffoldState,
                onPopBackStack = navController::popBackStack
            )
        }
        composable(Screen.MainFeedScreen.route) {
            MainFeedScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(Screen.ChatScreen.route) {
            ChatScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(
            route = Screen.MessageScreen.route + "/{remoteUserId}/{remoteUsername}/{remoteUserProfilePictureUrl}?chatId={chatId}",
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("remoteUserId") {
                    type = NavType.StringType
                },
                navArgument("remoteUsername") {
                    type = NavType.StringType
                },
                navArgument("remoteUserProfilePictureUrl") {
                    type = NavType.StringType
                }
            )
        ) {
            val remoteUserId = it.arguments?.getString("remoteUserId")!!
            val remoteUsername = it.arguments?.getString("remoteUsername")!!
            val remoteUserProfilePictureUrl = it.arguments?.getString("remoteUserProfilePictureUrl")!!
            MessageScreen(
                remoteUserId = remoteUserId,
                remoteUsername = remoteUsername,
                scaffoldState = scaffoldState,
                encodedRemoteUserProfilePictureUrl = remoteUserProfilePictureUrl,
                onNavigateUp = navController::navigateUp,
                onNavigate = navController::navigate,
                imageLoader = imageLoader
            )
        }
        composable(Screen.ActivityScreen.route) {
            ActivityScreen(
                imageLoader = imageLoader,
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
                userId = it.arguments?.getString("userId"),
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                onLogout = {
                    navController.popBackStack(
                        route = Screen.MainFeedScreen.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.LoginScreen.route)
                },
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(Screen.CreatePostScreen.route) {
            CreatePostScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(
            route = Screen.EditProfileScreen.route + "/{userId}",
            arguments = listOf(
                navArgument(name = "userId") {
                    type = NavType.StringType
                }
            )
        ) {
            EditProfileScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                imageLoader = imageLoader,
            )
        }
        composable(
            route = Screen.PersonListScreen.route + "/{parentId}",
            arguments = listOf(
                navArgument("parentId") {
                    type = NavType.StringType
                }
            )
        ) {
            PersonListScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(
            route = Screen.PostDetailScreen.route + "/{postId}?shouldShowKeyboard={shouldShowKeyboard}",
            arguments = listOf(
                navArgument(
                    name = "postId"
                ) {
                    type = NavType.StringType
                },
                navArgument(
                    name = "shouldShowKeyboard"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    action = Intent.ACTION_VIEW
                    uriPattern = "https://connectify.com/{postId}"
                }
            )
        ) {
            val shouldShowKeyboard = it.arguments?.getBoolean("shouldShowKeyboard") ?: false
            PostDetailScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                shouldShowKeyboard = shouldShowKeyboard,
                imageLoader = imageLoader
            )
        }
        composable(
            route = Screen.FollowingScreen.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) {
            FollowingScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
        composable(
            route = Screen.FollowerScreen.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) {
            FollowerScreen(
                onNavigate = navController::navigate,
                onNavigateUp = navController::navigateUp,
                scaffoldState = scaffoldState,
                imageLoader = imageLoader
            )
        }
    }
}