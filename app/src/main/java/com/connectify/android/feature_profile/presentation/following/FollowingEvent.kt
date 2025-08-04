package com.connectify.android.feature_profile.presentation.following

sealed interface FollowingEvent {
    data class OnToggleFollowStateForUser(val userId: String): FollowingEvent
}