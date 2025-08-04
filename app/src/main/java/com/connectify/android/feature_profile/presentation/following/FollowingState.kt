package com.connectify.android.feature_profile.presentation.following

import com.connectify.android.core.domain.models.UserItem

data class FollowingState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)