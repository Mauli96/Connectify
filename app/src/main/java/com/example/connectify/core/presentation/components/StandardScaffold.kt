package com.example.connectify.core.presentation.components

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connectify.R
import com.example.connectify.core.domain.models.BottomNavItem
import com.example.connectify.core.util.Screen

@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    showBottomBar: Boolean = true,
    snackbarHost:  @Composable () -> Unit = {},
    bottomNavItem: List<BottomNavItem> = listOf(
        BottomNavItem(
            route = Screen.MainFeedScreen.route,
            icon = painterResource(id = R.drawable.ic_home),
            contentDescription = "Home"
        ),
        BottomNavItem(
            route = Screen.ChatScreen.route,
            icon = painterResource(id = R.drawable.ic_message),
            contentDescription = "Message"
        ),
        BottomNavItem(
            route = Screen.CreatePostScreen.route,
            icon = painterResource(id = R.drawable.ic_post),
            contentDescription = "Post"
        ),
        BottomNavItem(
            route = Screen.ActivityScreen.route,
            icon = painterResource(id = R.drawable.ic_notification),
            contentDescription = "Activity"
        ),
        BottomNavItem(
            route = Screen.ProfileScreen.route,
            icon = painterResource(id = R.drawable.ic_person),
            contentDescription = "Profile"
        )
    ),
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = snackbarHost,
        bottomBar = {
            if(showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    setShadowLayer(
                                        36f,
                                        0f,
                                        12f,
                                        android.graphics.Color.argb(150, 0, 0, 0)
                                    )
                                }
                                canvas.nativeCanvas.drawRect(
                                    0f,
                                    0f,
                                    size.width,
                                    size.height,
                                    paint
                                )
                            }
                        },
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    bottomNavItem.forEachIndexed { _, bottomNavItem ->
                        StandardBottomNavItem(
                            icon = bottomNavItem.icon,
                            contentDescription = bottomNavItem.contentDescription,
                            selected = navController.currentDestination?.route?.startsWith(bottomNavItem.route) == true,
                            alertCount = bottomNavItem.alertCount,
                            enabled = bottomNavItem.icon != null
                        ) {
                            if(navController.currentDestination?.route != bottomNavItem.route) {
                                navController.navigate(bottomNavItem.route)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues  ->
        content(paddingValues)
    }
}