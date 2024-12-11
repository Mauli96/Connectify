package com.example.connectify.core.domain.states

data class PagingState<T>(
    val items: List<T> = emptyList(),
    val isFirstLoading: Boolean = false,
    val isNextLoading: Boolean = false,
    val endReached: Boolean = false
)