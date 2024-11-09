package com.example.connectify.feature_activity.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.feature_activity.presentation.components.ActivityItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ActivityScreen(
    scaffoldState: ScaffoldState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val state by viewModel.pagingState.collectAsStateWithLifecycle()
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                title = {
                    Text(
                        text = stringResource(id = R.string.activity),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                showBackArrow = false,
            )
            LazyColumn {
                items(
                    count = state.items.size,
                    key = { i ->
                        val activity = state.items[i]
                        activity.id
                    }
                ) { i ->
                    val activity = state.items[i]
                    if(i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                        viewModel.loadNextActivities()
                    }
                    ActivityItem(
                        activity = Activity(
                            id = activity.id,
                            userId = activity.userId,
                            activityType = activity.activityType,
                            formattedTime = activity.formattedTime,
                            parentId = activity.parentId,
                            username = activity.username,
                            profilePictureUrl = activity.profilePictureUrl
                        ),
                        onNavigate = onNavigate,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .height(0.5.dp),
                        thickness = 1.dp,
                        color = HintGray
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
        if(state.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}