package com.example.connectify.feature_profile.presentation.following

import com.example.connectify.core.domain.models.UserItem

data class FollowingState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)