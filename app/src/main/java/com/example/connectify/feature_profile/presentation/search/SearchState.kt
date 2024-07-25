package com.example.connectify.feature_profile.presentation.search

import com.example.connectify.core.domain.models.UserItem

data class SearchState(
    val userItems: List<UserItem> = emptyList(),
    val isLoading: Boolean = false,
)