package com.connectify.android.core.util

import com.connectify.android.core.domain.models.Comment

interface CommentLiker {

    suspend fun toggleLike(
        comments: List<Comment>,
        parentId: String,
        onRequest: suspend (isLiked: Boolean) -> SimpleResource,
        onStateUpdated: (List<Comment>) -> Unit
    )
}