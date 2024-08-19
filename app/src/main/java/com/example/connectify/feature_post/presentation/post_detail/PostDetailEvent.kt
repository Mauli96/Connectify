package com.example.connectify.feature_post.presentation.post_detail

sealed class PostDetailEvent {
    data class LikeComment(val commentId: String): PostDetailEvent()
    data class EnteredComment(val comment: String): PostDetailEvent()
    data class DeleteComment(val commentId: String): PostDetailEvent()
    data class GetDeleteCommentId(val commentId: String): PostDetailEvent()
    object LikePost: PostDetailEvent()
    object Comment: PostDetailEvent()
    object ShowBottomSheet: PostDetailEvent()
    object DismissBottomSheet: PostDetailEvent()
}
