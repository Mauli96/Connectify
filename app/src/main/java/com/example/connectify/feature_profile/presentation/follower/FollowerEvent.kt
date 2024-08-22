package com.example.connectify.feature_profile.presentation.follower

sealed class FollowerEvent {
    data class ToggleFollowStateForUser(val userId: String): FollowerEvent()
}