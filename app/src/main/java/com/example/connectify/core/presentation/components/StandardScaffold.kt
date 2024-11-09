package com.example.connectify.core.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connectify.R
import com.example.connectify.core.domain.models.BottomNavItem
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.util.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StandardScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    showBottomBar: Boolean = true,
    state: ScaffoldState,
    snackbarHost:  @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
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
        BottomNavItem(route = "-"),
        BottomNavItem(
            route = Screen.ActivityScreen.route,
            icon = painterResource(id = R.drawable.notification_icon),
            contentDescription = "Activity"
        ),
        BottomNavItem(
            route = Screen.ProfileScreen.route,
            icon = painterResource(id = R.drawable.person_icon),
            contentDescription = "Profile"
        ),
    ),
    onFabClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if(showBottomBar) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    cutoutShape = CircleShape,
                    elevation = 50.dp
                ) {
                    BottomNavigation(
                        backgroundColor = MaterialTheme.colorScheme.background
                    ) {
                        bottomNavItem.forEachIndexed { i, bottomNavItem ->
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
        },
        scaffoldState = state,
        snackbarHost = snackbarHost,
        floatingActionButton = {
            if(showBottomBar) {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = onFabClick,
                    shape = CircleShape,
                ) {
                    Image(
                        painter = painterResource(R.drawable.plus_icon),
                        contentDescription = stringResource(id = R.string.make_post),
                        modifier = Modifier.size(IconSizeSmall)
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) {
        content()
    }
}