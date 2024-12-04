package com.example.connectify.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connectify.R
import com.example.connectify.core.domain.models.BottomNavItem
import com.example.connectify.core.presentation.ui.theme.HintGray
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
            icon = painterResource(id = R.drawable.home_icon),
            contentDescription = "Home"
        ),
        BottomNavItem(
            route = Screen.ChatScreen.route,
            icon = painterResource(id = R.drawable.message_icon),
            contentDescription = "Message"
        ),
        BottomNavItem(
            route = Screen.CreatePostScreen.route,
            icon = painterResource(id = R.drawable.post_icon),
            contentDescription = "Post"
        ),
        BottomNavItem(
            route = Screen.ActivityScreen.route,
            icon = painterResource(id = R.drawable.notification_icon),
            contentDescription = "Activity"
        ),
        BottomNavItem(
            route = Screen.ProfileScreen.route,
            icon = painterResource(id = R.drawable.person_icon),
            contentDescription = "Profile"
        )
    ),
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            snackbarHost()
        },
        bottomBar = {
            if(showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .drawBehind {
                            drawLine(
                                color = HintGray,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 50.dp
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            content()
        }
    }
}