package com.connectify.android.feature_profile.presentation.search

import com.connectify.android.core.domain.models.UserItem

data class SearchState(
    val userItems: List<UserItem> = emptyList(),
    val isLoading: Boolean = false,
)