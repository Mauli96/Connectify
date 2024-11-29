package com.example.connectify.feature_profile.presentation.follower

import com.example.connectify.core.domain.models.UserItem

data class FollowerState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)