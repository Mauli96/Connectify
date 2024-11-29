package com.example.connectify.core.util

import com.example.connectify.core.domain.models.Post

interface PostSaver {

    suspend fun toggleSave(
        posts: List<Post>,
        parentId: String,
        onRequest: suspend (isSaved: Boolean) -> SimpleResource,
        onStateUpdated: (List<Post>) -> Unit
    )
}