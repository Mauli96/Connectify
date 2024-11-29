package com.example.connectify.feature_profile.presentation.search

sealed class SearchEvent {
    data class Query(val query: String): SearchEvent()
    data class ToggleFollow(val userId: String): SearchEvent()
    data class OnToggleSearch(val query: String): SearchEvent()
}