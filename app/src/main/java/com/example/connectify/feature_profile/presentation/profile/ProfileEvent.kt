package com.example.connectify.feature_profile.presentation.profile

import com.example.connectify.feature_post.presentation.util.CommentFilter


sealed interface ProfileEvent {
    data class OnToggleFollowStateForUser(val userId: String): ProfileEvent
    data class OnLikedPost(val postId: String): ProfileEvent
    data class OnLikedComment(val commentId: String): ProfileEvent
    data class OnEnteredComment(val comment: String): ProfileEvent
    data class OnSavePost(val postId: String): ProfileEvent
    data class OnSelectPostId(val postId: String): ProfileEvent
    data class OnSelectPostUsername(val postUsername: String, val isOwnPost: Boolean): ProfileEvent
    data class OnSelectComment(val commentId: String): ProfileEvent
    data class OnChangeCommentFilter(val filterType: CommentFilter): ProfileEvent
    data class OnDescriptionToggle(val postId: String): ProfileEvent
    data object OnDeletePost: ProfileEvent
    data object OnDownloadPost: ProfileEvent
    data object OnLoadComments: ProfileEvent
    data object OnComment: ProfileEvent
    data object OnDeleteComment: ProfileEvent
    data object OnShowFilterMenu: ProfileEvent
    data object OnDismissFilterMenu: ProfileEvent
    data object OnShowDeleteSheet: ProfileEvent
    data object OnDismissDeleteSheet: ProfileEvent
    data object OnShowBottomSheet: ProfileEvent
    data object OnDismissBottomSheet: ProfileEvent
    data object OnShowDropDownMenu: ProfileEvent
    data object OnDisMissDropDownMenu: ProfileEvent
    data object OnShowLogoutDialog: ProfileEvent
    data object OnDismissLogoutDialog: ProfileEvent
    data object OnNavigatedToPersonListScreen: ProfileEvent
    data object OnLogout: ProfileEvent
}