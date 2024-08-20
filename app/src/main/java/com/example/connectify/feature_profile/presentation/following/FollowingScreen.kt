package com.example.connectify.feature_profile.presentation.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.components.UserProfileItem
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Screen
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@Composable
fun FollowingScreen(
    scaffoldState: ScaffoldState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: FollowingViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        event.uiText.asString(context)
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            onNavigateUp = onNavigateUp,
            showBackArrow = true,
            title = {
                Text(
                    text = stringResource(id = R.string.following),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.users) { user ->
                    UserProfileItem(
                        user = user,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth(),
                        isFollowing = user.isFollowing,
                        onItemClick = {
                            onNavigate(Screen.ProfileScreen.route + "?userId=${user.userId}")
                        },
                        onActionItemClick = {
                            viewModel.onEvent(FollowingEvent.ToggleFollowStateForUser(user.userId))
                        },
                        ownUserId = viewModel.ownUserId.value
                    )
                }
            }
            if(state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Center),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                    trackColor = Color.White
                )
            }
        }
    }
}