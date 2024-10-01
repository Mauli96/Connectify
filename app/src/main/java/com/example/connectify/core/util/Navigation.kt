package com.example.connectify.core.util

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.ImageLoader
import com.example.connectify.feature_activity.presentation.ActivityScreen
import com.example.connectify.feature_auth.presentation.login.LoginScreen
import com.example.connectify.feature_auth.presentation.register.RegisterScreen
import com.example.connectify.feature_chat.presentation.chat.ChatScreen
import com.example.connectify.feature_chat.presentation.message.MessageScreen
import com.example.connectify.feature_post.presentation.create_post.CreatePostScreen
import com.example.connectify.feature_post.presentation.main_feed.MainFeedScreen
import com.example.connectify.feature_post.presentation.person_list.PersonListScreen
import com.example.connectify.feature_profile.presentation.edit_profile.EditProfileScreen
import com.example.connectify.feature_profile.presentation.follower.FollowerScreen
import com.example.connectify.feature_profile.presentation.following.FollowingScreen
import com.example.connectify.feature_profile.presentation.profile.ProfileScreen
import com.example.connectify.feature_profile.presentation.search.SearchScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    imageLoader: ImageLoader,
    isUserAuthenticated: Boolean?
) {
    val startDestination = when(isUserAuthenticated) {
        true -> Screen.MainFeedScreen.route
        false -> Screen.LoginScreen.route
        else -> null
    }
    val animationDuration = 500

    startDestination?.let {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
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
            composable(
                route = Screen.MainFeedScreen.route,
                exitTransition = {
                    if(targetState.destination.route == Screen.SearchScreen.route) {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animationDuration))
                    } else {
                        null
                    }
                },
                popEnterTransition = {
                    if(initialState.destination.route == Screen.SearchScreen.route) {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animationDuration))
                    } else {
                        null
                    }
                }
            ) {
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
                route = Screen.MessageScreen.route + "/{remoteUserId}/{remoteUsername}/{remoteUserProfilePictureUrl}?chatId={chatId}?isOnline={isOnline}?lastSeen={lastSeen}",
                arguments = listOf(
                    navArgument("chatId") {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument("isOnline") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("lastSeen") {
                        type = NavType.LongType
                        defaultValue = 0L
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
                ),
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animationDuration))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animationDuration))
                }
            ) {
                val remoteUserId = it.arguments?.getString("remoteUserId")!!
                val remoteUsername = it.arguments?.getString("remoteUsername")!!
                val remoteUserProfilePictureUrl = it.arguments?.getString("remoteUserProfilePictureUrl")!!
                val isOnline = it.arguments?.getBoolean("isOnline") ?: false
                val lastSeen = it.arguments?.getLong("lastSeen") ?: 0L
                MessageScreen(
                    remoteUserId = remoteUserId,
                    remoteUsername = remoteUsername,
                    isOnline = isOnline,
                    lastSeen = lastSeen,
                    scaffoldState = scaffoldState,
                    encodedRemoteUserProfilePictureUrl = remoteUserProfilePictureUrl,
                    onNavigateUp = navController::navigateUp,
                    onNavigate = navController::navigate,
                    imageLoader = imageLoader
                )
            }
            composable(Screen.ActivityScreen.route) {
                ActivityScreen(
                    scaffoldState = scaffoldState,
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
            composable(
                route = Screen.SearchScreen.route,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animationDuration))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animationDuration))
                },
                popEnterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animationDuration))
                },
                popExitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animationDuration))
                }
            ) {
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
}