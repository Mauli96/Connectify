package com.example.connectify.feature_profile.presentation.profile

sealed class ProfileEvent {
    data class LikePost(val postId: String): ProfileEvent()
}