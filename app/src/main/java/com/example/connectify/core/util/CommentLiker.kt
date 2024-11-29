package com.example.connectify.core.util

import com.example.connectify.core.domain.models.Comment

interface CommentLiker {

    suspend fun toggleLike(
        comments: List<Comment>,
        parentId: String,
        onRequest: suspend (isLiked: Boolean) -> SimpleResource,
        onStateUpdated: (List<Comment>) -> Unit
    )
}