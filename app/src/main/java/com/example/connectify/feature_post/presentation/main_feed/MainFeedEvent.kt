package com.example.connectify.feature_post.presentation.main_feed

import com.example.connectify.feature_post.presentation.util.CommentFilter

sealed class MainFeedEvent {
    data class LikedPost(val postId: String): MainFeedEvent()
    data class LikedComment(val commentId: String): MainFeedEvent()
    data class EnteredComment(val comment: String): MainFeedEvent()
    data class SavePost(val postId: String): MainFeedEvent()
    data class SelectPostId(val postId: String): MainFeedEvent()
    data class SelectPostUsername(val postUsername: String, val isOwnPost: Boolean): MainFeedEvent()
    data class SelectComment(val commentId: String): MainFeedEvent()
    data class ChangeCommentFilter(val filterType: CommentFilter): MainFeedEvent()
    data class OnDescriptionToggle(val postId: String): MainFeedEvent()
    object DeletePost: MainFeedEvent()
    object DownloadPost: MainFeedEvent()
    object LoadComments: MainFeedEvent()
    object Comment: MainFeedEvent()
    object DeleteComment: MainFeedEvent()
    object ShowDropDownMenu: MainFeedEvent()
    object DismissDropDownMenu: MainFeedEvent()
    object ShowBottomSheet: MainFeedEvent()
    object DismissBottomSheet: MainFeedEvent()
    object ShowCommentBottomSheet: MainFeedEvent()
    object DismissCommentBottomSheet: MainFeedEvent()
    object NavigatedToSearchScreen: MainFeedEvent()
    object NavigatedToPersonListScreen: MainFeedEvent()
}