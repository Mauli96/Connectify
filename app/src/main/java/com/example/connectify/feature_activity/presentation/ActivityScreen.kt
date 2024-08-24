package com.example.connectify.feature_activity.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.feature_activity.presentation.components.ActivityItem

@Composable
fun ActivityScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val activities = viewModel.activities.collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary),
            ) {
                items(
                    count = activities.itemCount,
                    key = activities.itemKey { it.id },
                    contentType = activities.itemContentType { "contentType" }
                ) { index ->
                    val activity = activities[index]
                    activity?.let {
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
                    }
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
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}