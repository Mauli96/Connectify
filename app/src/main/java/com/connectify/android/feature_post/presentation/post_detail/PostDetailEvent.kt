package com.connectify.android.feature_post.presentation.post_detail

import com.connectify.android.feature_post.presentation.util.CommentFilter

sealed interface PostDetailEvent {
    data class OnLikeComment(val commentId: String): PostDetailEvent
    data class OnEnteredComment(val comment: String): PostDetailEvent
    data class OnSavePost(val postId: String): PostDetailEvent
    data class OnSelectPostUsername(val postUsername: String, val isOwnPost: Boolean): PostDetailEvent
    data class OnSelectComment(val commentId: String): PostDetailEvent
    data class OnChangeCommentFilter(val filterType: CommentFilter): PostDetailEvent
    data object OnDownloadPost: PostDetailEvent
    data object OnLikePost: PostDetailEvent
    data object OnComment: PostDetailEvent
    data object OnDeletePost: PostDetailEvent
    data object OnDeleteComment: PostDetailEvent
    data object OnShowBottomSheet: PostDetailEvent
    data object OnDismissBottomSheet: PostDetailEvent
    data object OnDescriptionToggle: PostDetailEvent
    data object OnShowDropDownMenu: PostDetailEvent
    data object OnDismissDropDownMenu: PostDetailEvent
}