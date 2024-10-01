package com.example.connectify.feature_post.presentation.main_feed

import com.example.connectify.feature_post.presentation.util.CommentFilter

sealed class MainFeedEvent {
    data class LikedPost(val postId: String): MainFeedEvent()
    data class LikedComment(val commentId: String): MainFeedEvent()
    data class EnteredComment(val comment: String): MainFeedEvent()
    data class SelectPost(val postId: String): MainFeedEvent()
    data class SelectComment(val commentId: String): MainFeedEvent()
    data class ChangeCommentFilter(val filterType: CommentFilter): MainFeedEvent()
    data class OnDescriptionToggle(val postId: String): MainFeedEvent()
    object DeletePost: MainFeedEvent()
    object LoadComments: MainFeedEvent()
    object Comment: MainFeedEvent()
    object DeleteComment: MainFeedEvent()
    object ShowDropDownMenu: MainFeedEvent()
    object DismissDropDownMenu: MainFeedEvent()
    object ShowBottomSheet: MainFeedEvent()
    object DismissBottomSheet: MainFeedEvent()
    object NavigatedToSearchScreen: MainFeedEvent()
    object NavigatedToPersonListScreen: MainFeedEvent()
}