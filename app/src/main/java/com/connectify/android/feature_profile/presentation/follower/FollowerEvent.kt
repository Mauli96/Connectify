package com.connectify.android.feature_profile.presentation.follower

sealed interface FollowerEvent {
    data class OnToggleFollowStateForUser(val userId: String): FollowerEvent
}