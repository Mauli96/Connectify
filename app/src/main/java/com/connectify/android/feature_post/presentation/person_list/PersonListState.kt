package com.connectify.android.feature_post.presentation.person_list

import com.connectify.android.core.domain.models.UserItem

data class PersonListState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)