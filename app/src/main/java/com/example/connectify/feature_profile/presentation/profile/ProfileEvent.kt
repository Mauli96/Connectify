package com.example.connectify.feature_profile.presentation.profile

import com.example.connectify.feature_post.presentation.util.CommentFilter


sealed class ProfileEvent {
    data class ToggleFollowStateForUser(val userId: String): ProfileEvent()
    data class LikedPost(val postId: String): ProfileEvent()
    data class LikedComment(val commentId: String): ProfileEvent()
    data class EnteredComment(val comment: String): ProfileEvent()
    data class SavePost(val postId: String): ProfileEvent()
    data class SelectPostId(val postId: String): ProfileEvent()
    data class SelectPostUsername(val postUsername: String, val isOwnPost: Boolean): ProfileEvent()
    data class SelectComment(val commentId: String): ProfileEvent()
    data class ChangeCommentFilter(val filterType: CommentFilter): ProfileEvent()
    data class OnDescriptionToggle(val postId: String): ProfileEvent()
    object DeletePost: ProfileEvent()
    object DownloadPost: ProfileEvent()
    object LoadComments: ProfileEvent()
    object Comment: ProfileEvent()
    object DeleteComment: ProfileEvent()
    object ShowFilterMenu: ProfileEvent()
    object DismissFilterMenu: ProfileEvent()
    object ShowDeleteSheet: ProfileEvent()
    object DismissDeleteSheet: ProfileEvent()
    object ShowBottomSheet: ProfileEvent()
    object DismissBottomSheet: ProfileEvent()
    object ShowDropDownMenu: ProfileEvent()
    object DisMissDropDownMenu: ProfileEvent()
    object ShowLogoutDialog: ProfileEvent()
    object DismissLogoutDialog: ProfileEvent()
    object NavigatedToPersonListScreen: ProfileEvent()
    object Logout: ProfileEvent()
}