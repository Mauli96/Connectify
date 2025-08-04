package com.connectify.android.core.util

import com.connectify.android.core.domain.models.Post

interface PostSaver {

    suspend fun toggleSave(
        posts: List<Post>,
        parentId: String,
        onRequest: suspend (isSaved: Boolean) -> SimpleResource,
        onStateUpdated: (List<Post>) -> Unit
    )
}