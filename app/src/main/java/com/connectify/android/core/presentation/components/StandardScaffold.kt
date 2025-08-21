package com.connectify.android.core.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.connectify.android.R
import com.connectify.android.core.domain.models.BottomNavItem
import com.connectify.android.core.util.Screen

@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    showBottomBar: Boolean = true,
    snackbarHost: @Composable () -> Unit = {},
    bottomNavItem: List<BottomNavItem> = listOf(
        BottomNavItem(
            route = Screen.MainFeedScreen.route,
            icon = painterResource(id = R.drawable.ic_home),
            contentDescription = stringResource(R.string.home)
        ),
        BottomNavItem(
            route = Screen.ChatScreen.route,
            icon = painterResource(id = R.drawable.ic_message),
            contentDescription = stringResource(R.string.message)
        ),
        BottomNavItem(
            route = Screen.CreatePostScreen.route,
            icon = painterResource(id = R.drawable.ic_post),
            contentDescription = stringResource(R.string.post)
        ),
        BottomNavItem(
            route = Screen.ActivityScreen.route,
            icon = painterResource(id = R.drawable.ic_notification),
            contentDescription = stringResource(R.string.activity)
        ),
        BottomNavItem(
            route = Screen.ProfileScreen.route,
            icon = painterResource(id = R.drawable.ic_person),
            contentDescription = stringResource(R.string.profile)
        )
    ),
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = snackbarHost,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    setShadowLayer(
                                        /* radius = */ 36f,
                                        /* dx = */ 0f,
                                        /* dy = */ 12f,
                                        /* shadowColor = */ android.graphics.Color.argb(150, 0, 0, 0)
                                    )
                                }
                                canvas.nativeCanvas.drawRect(
                                    0f, 0f, size.width, size.height, paint
                                )
                            }
                        },
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    val navBackStackEntry = navController.currentBackStackEntryAsState().value
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItem.forEach { item ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { dest ->
                                dest.route == item.route ||
                                        (dest.route?.startsWith(item.route) == true)
                            } == true

                        StandardBottomNavItem(
                            icon = item.icon,
                            contentDescription = item.contentDescription,
                            selected = selected,
                            alertCount = item.alertCount,
                            enabled = item.icon != null
                        ) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}