package com.example.connectify.feature_post.presentation.person_list

import com.example.connectify.core.domain.models.UserItem

data class PersonListState(
    val users: List<UserItem> = emptyList(),
    val isLoading: Boolean = false
)