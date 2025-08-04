package com.connectify.android.core.util

import com.connectify.android.core.domain.models.Comment

class DefaultCommentLiker : CommentLiker {

    override suspend fun toggleLike(
        comments: List<Comment>,
        parentId: String,
        onRequest: suspend (isLiked: Boolean) -> SimpleResource,
        onStateUpdated: (List<Comment>) -> Unit
    ) {
        val comment = comments.find { it.id == parentId }
        val currentlyLiked = comment?.isLiked == true
        val currentLikeCount = comment?.likeCount ?: 0
        val newPosts = comments.map { comment ->
            if(comment.id == parentId) {
                comment.copy(
                    isLiked = !comment.isLiked,
                    likeCount = if(currentlyLiked) {
                        comment.likeCount.minus(1)
                    } else comment.likeCount.plus(1)
                )
            } else comment
        }
        onStateUpdated(newPosts)
        val result = onRequest(currentlyLiked)
        when(result) {
            is Resource.Success -> Unit
            is Resource.Error -> {
                val oldPosts = comments.map { comment ->
                    if(comment.id == parentId) {
                        comment.copy(
                            isLiked = currentlyLiked,
                            likeCount = currentLikeCount
                        )
                    } else comment
                }
                onStateUpdated(oldPosts)
            }
        }
    }
}