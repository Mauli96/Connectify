package com.example.connectify.feature_post.presentation.post_detail

import com.example.connectify.feature_post.presentation.util.CommentFilter

sealed class PostDetailEvent {
    data class LikeComment(val commentId: String): PostDetailEvent()
    data class EnteredComment(val comment: String): PostDetailEvent()
    data class SelectComment(val commentId: String): PostDetailEvent()
    data class ChangeCommentFilter(val filterType: CommentFilter): PostDetailEvent()
    object LikePost: PostDetailEvent()
    object Comment: PostDetailEvent()
    object DeleteComment: PostDetailEvent()
    object ShowDropDownMenu: PostDetailEvent()
    object DismissDropDownMenu: PostDetailEvent()
    object ShowBottomSheet: PostDetailEvent()
    object DismissBottomSheet: PostDetailEvent()
}
