package com.example.connectify.feature_post.presentation.post_detail

import com.example.connectify.feature_post.presentation.util.CommentFilter

sealed class PostDetailEvent {
    data class LikeComment(val commentId: String): PostDetailEvent()
    data class EnteredComment(val comment: String): PostDetailEvent()
    data class SavePost(val postId: String): PostDetailEvent()
    data class SelectPostUsername(val postUsername: String, val isOwnPost: Boolean): PostDetailEvent()
    data class SelectComment(val commentId: String): PostDetailEvent()
    data class ChangeCommentFilter(val filterType: CommentFilter): PostDetailEvent()
    object DownloadPost: PostDetailEvent()
    object LikePost: PostDetailEvent()
    object Comment: PostDetailEvent()
    object DeletePost: PostDetailEvent()
    object DeleteComment: PostDetailEvent()
    object ShowBottomSheet: PostDetailEvent()
    object DismissBottomSheet: PostDetailEvent()
    object OnDescriptionToggle: PostDetailEvent()
    object ShowDropDownMenu: PostDetailEvent()
    object DismissDropDownMenu: PostDetailEvent()
}