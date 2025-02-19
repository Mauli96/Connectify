package com.example.connectify.feature_profile.presentation.search

sealed interface SearchEvent {
    data class OnQuery(val query: String): SearchEvent
    data class OnToggleFollow(val userId: String): SearchEvent
    data class OnToggleSearch(val query: String): SearchEvent
}