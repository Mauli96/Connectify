package com.connectify.android.feature_profile.presentation.search

sealed interface SearchEvent {
    data class OnQuery(val query: String): SearchEvent
    data class OnToggleFollow(val userId: String): SearchEvent
    data class OnClearSearch(val query: String): SearchEvent
}