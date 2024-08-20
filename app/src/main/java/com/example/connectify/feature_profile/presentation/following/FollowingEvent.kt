package com.example.connectify.feature_profile.presentation.following

sealed class FollowingEvent {
    data class ToggleFollowStateForUser(val userId: String): FollowingEvent()
}