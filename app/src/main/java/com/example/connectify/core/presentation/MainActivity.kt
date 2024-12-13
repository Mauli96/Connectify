package com.example.connectify.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.example.connectify.core.presentation.components.CustomSnackbarHost
import com.example.connectify.core.presentation.components.StandardScaffold
import com.example.connectify.core.presentation.ui.theme.ConnectifyTheme
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Navigation
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_auth.presentation.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel by viewModels<SplashViewModel>()
    @Inject
    lateinit var imageLoader: ImageLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.keepSplashScreenOn.value
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConnectifyTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val snackbarHostState = remember { SnackbarHostState() }
                    val isUserAuthenticated by splashViewModel.isUserAuthenticated.collectAsStateWithLifecycle()
                    val splashDuration = if(isUserAuthenticated == true) 100L else Constants.SPLASH_SCREEN_DURATION

                    LaunchedEffect(isUserAuthenticated) {
                        splashViewModel.eventFlow.collectLatest { event ->
                            when(event) {
                                is UiEvent.Navigate -> {
                                    delay(splashDuration)
                                    navController.navigate(event.route) {
                                        popUpTo(0)
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }

                    StandardScaffold(
                        navController = navController,
                        showBottomBar = shouldShowBottomBar(navBackStackEntry),
                        snackbarHost = {
                            CustomSnackbarHost(
                                snackbarHostState = snackbarHostState,
                                onNavigate = navController::navigate
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Navigation(
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            imageLoader = imageLoader,
                            isUserAuthenticated = isUserAuthenticated
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowBottomBar(backStackEntry: NavBackStackEntry?): Boolean {
        val doesRouteMatch = backStackEntry?.destination?.route in listOf(
            Screen.MainFeedScreen.route,
            Screen.ChatScreen.route,
            Screen.ActivityScreen.route
        )
        val isOwnProfile = backStackEntry?.destination?.route == "${Screen.ProfileScreen.route}?userId={userId}" &&
                backStackEntry.arguments?.getString("userId") == null
        return doesRouteMatch || isOwnProfile
    }
}