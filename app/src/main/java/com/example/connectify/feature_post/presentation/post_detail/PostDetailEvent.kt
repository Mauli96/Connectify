package com.example.connectify.feature_post.presentation.post_detail

sealed class PostDetailEvent {
    object LikePost: PostDetailEvent()
    object Comment: PostDetailEvent()
    data class LikeComment(val commentId: String): PostDetailEvent()
    data class EnteredComment(val comment: String): PostDetailEvent()
}
