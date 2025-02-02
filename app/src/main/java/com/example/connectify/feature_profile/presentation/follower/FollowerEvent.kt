package com.example.connectify.feature_profile.presentation.follower

sealed interface FollowerEvent {
    data class OnToggleFollowStateForUser(val userId: String): FollowerEvent
}