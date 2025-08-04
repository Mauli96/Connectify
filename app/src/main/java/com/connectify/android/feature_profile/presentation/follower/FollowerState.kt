package com.connectify.android.feature_profile.presentation.follower

import com.connectify.android.core.domain.models.UserItem

data class FollowerState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)