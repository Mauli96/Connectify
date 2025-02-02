package com.example.connectify.feature_profile.presentation.following

sealed interface FollowingEvent {
    data class OnToggleFollowStateForUser(val userId: String): FollowingEvent
}