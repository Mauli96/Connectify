package com.connectify.android.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.connectify.android.core.presentation.components.CustomSnackbarHost
import com.connectify.android.core.presentation.components.StandardScaffold
import com.connectify.android.core.presentation.ui.theme.ConnectifyTheme
import com.connectify.android.core.util.Navigation
import com.connectify.android.core.util.Screen
import com.connectify.android.feature_auth.presentation.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel by viewModels<SplashViewModel>()
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.splashState.value.keepSplashScreenOn
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            ConnectifyTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val splashState by splashViewModel.splashState.collectAsStateWithLifecycle()

                    if(!splashState.keepSplashScreenOn && splashState.startDestination.isNotEmpty()) {
                        MainContent(
                            startDestination = splashState.startDestination,
                            imageLoader = imageLoader
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MainContent(
        startDestination: String,
        imageLoader: ImageLoader
    ) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val snackbarHostState = remember { SnackbarHostState() }

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
        ) { paddingValues ->
            Navigation(
                navController = navController,
                snackbarHostState = snackbarHostState,
                imageLoader = imageLoader,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            )
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