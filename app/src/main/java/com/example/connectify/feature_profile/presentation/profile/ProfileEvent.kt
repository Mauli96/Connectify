package com.example.connectify.feature_profile.presentation.profile


sealed class ProfileEvent {
    data class LikePost(val postId: String): ProfileEvent()
    data class DeletePost(val postId: String): ProfileEvent()
    data class ToggleFollowStateForUser(val userId: String): ProfileEvent()
    data class DeletePostId(val postId: String): ProfileEvent()
    object ShowBottomSheet: ProfileEvent()
    object DismissBottomSheet: ProfileEvent()
    object ShowLogoutDialog: ProfileEvent()
    object DismissLogoutDialog: ProfileEvent()
    object Logout: ProfileEvent()
}