package com.example.connectify.core.util

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.ImageLoader
import com.example.connectify.feature_activity.presentation.ActivityScreen
import com.example.connectify.feature_auth.presentation.login.LoginScreen
import com.example.connectify.feature_auth.presentation.register.RegisterScreen
import com.example.connectify.feature_chat.presentation.chat.ChatScreen
import com.example.connectify.feature_chat.presentation.message.MessageScreen
import com.example.connectify.feature_post.presentation.create_post.CreatePostScreen
import com.example.connectify.feature_post.presentation.main_feed.MainFeedScreen
import com.example.connectify.feature_post.presentation.person_list.PersonListScreen
import com.example.connectify.feature_post.presentation.post_detail.PostDetailScreen
import com.example.connectify.feature_post.presentation.save_post.SavedPostScreen
import com.example.connectify.feature_profile.presentation.edit_profile.EditProfileScreen
import com.example.connectify.feature_profile.presentation.follower.FollowerScreen
import com.example.connectify.feature_profile.presentation.following.FollowingScreen
import com.example.connectify.feature_profile.presentation.profile.ProfileScreen
import com.example.connectify.feature_profile.presentation.search.SearchScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    isUserAuthenticated: Boolean?
) {
    val startDestination = when(isUserAuthenticated) {
        true -> Screen.MainFeedScreen.route
        false -> Screen.AuthScreen.route
        else -> null
    }
    val animationDuration = 300

    startDestination?.let {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            navigation(
                startDestination = Screen.LoginScreen.route,
                route = Screen.AuthScreen.route
            ) {
                composable(
                    route = Screen.LoginScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    }
                ) {
                    LoginScreen(
                        onNavigate = navController::navigate,
                        onLogin = {
                            navController.navigate(Screen.MainFeedScreen.route) {
                                popUpTo(Screen.AuthScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(
                    route = Screen.RegisterScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(animationDuration)
                        )
                    }
                ) {
                    RegisterScreen(
                        onNavigate = navController::navigate,
                        onRegister = {
                            navController.navigate(Screen.MainFeedScreen.route) {
                                popUpTo(Screen.AuthScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
            composable(
                route = Screen.MainFeedScreen.route,
                exitTransition = {
                    if(targetState.destination.route == Screen.SearchScreen.route) {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(animationDuration)
                        )
                    } else {
                        null
                    }
                },
                popEnterTransition = {
                    if(initialState.destination.route == Screen.SearchScreen.route) {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationDuration)
                        )
                    } else {
                        null
                    }
                }
            ) {
                MainFeedScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(Screen.ChatScreen.route) {
                ChatScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
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
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
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
                    snackbarHostState = snackbarHostState,
                    encodedRemoteUserProfilePictureUrl = remoteUserProfilePictureUrl,
                    onNavigateUp = navController::navigateUp,
                    onNavigate = navController::navigate,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.ActivityScreen.route,
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                ActivityScreen(
                    snackbarHostState = snackbarHostState,
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
                ),
                deepLinks = listOf(
                    navDeepLink {
                        action = Intent.ACTION_VIEW
                        uriPattern = "https://connectify.com/profile"
                    }
                )
            ) {
                ProfileScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    onLogout = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.CreatePostScreen.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                CreatePostScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.PostDetailScreen.route + "/{postId}",
                arguments = listOf(
                    navArgument(
                        name = "postId"
                    ) {
                        type = NavType.StringType
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        action = Intent.ACTION_VIEW
                        uriPattern = "https://connectify.com/{postId}"
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                PostDetailScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.EditProfileScreen.route + "/{userId}",
                arguments = listOf(
                    navArgument(name = "userId") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                EditProfileScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.SearchScreen.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                SearchScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.PersonListScreen.route + "/{parentId}",
                arguments = listOf(
                    navArgument("parentId") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                PersonListScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.FollowingScreen.route + "/{userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                FollowingScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.FollowerScreen.route + "/{userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                FollowerScreen(
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                    imageLoader = imageLoader
                )
            }
            composable(
                route = Screen.SavedPostScreen.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(animationDuration)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(animationDuration)
                    )
                }
            ) {
                SavedPostScreen(
                    imageLoader = imageLoader,
                    onNavigate = navController::navigate,
                    onNavigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}