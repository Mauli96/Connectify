package com.example.connectify.feature_post.presentation.main_feed

import com.example.connectify.feature_post.presentation.util.CommentFilter

sealed interface MainFeedEvent {
    data class OnLikedPost(val postId: String): MainFeedEvent
    data class OnLikedComment(val commentId: String): MainFeedEvent
    data class OnEnteredComment(val comment: String): MainFeedEvent
    data class OnSavePost(val postId: String): MainFeedEvent
    data class OnSelectPostId(val postId: String): MainFeedEvent
    data class OnSelectPostUsername(val postUsername: String, val isOwnPost: Boolean): MainFeedEvent
    data class OnSelectComment(val commentId: String): MainFeedEvent
    data class OnChangeCommentFilter(val filterType: CommentFilter): MainFeedEvent
    data class OnDescriptionToggle(val postId: String): MainFeedEvent
    data object OnDeletePost: MainFeedEvent
    data object OnDownloadPost: MainFeedEvent
    data object OnLoadComments: MainFeedEvent
    data object OnComment: MainFeedEvent
    data object OnDeleteComment: MainFeedEvent
    data object OnShowDropDownMenu: MainFeedEvent
    data object OnDismissDropDownMenu: MainFeedEvent
    data object OnShowBottomSheet: MainFeedEvent
    data object OnDismissBottomSheet: MainFeedEvent
    data object OnShowCommentBottomSheet: MainFeedEvent
    data object OnDismissCommentBottomSheet: MainFeedEvent
    data object OnNavigatedToSearchScreen: MainFeedEvent
    data object OnNavigatedToPersonListScreen: MainFeedEvent
}