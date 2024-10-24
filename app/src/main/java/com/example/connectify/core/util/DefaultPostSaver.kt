package com.example.connectify.core.util

import com.example.connectify.core.domain.models.Post

class DefaultPostSaver : PostSaver {

    override suspend fun toggleSave(
        posts: List<Post>,
        parentId: String,
        onRequest: suspend (isSaved: Boolean) -> SimpleResource,
        onStateUpdated: (List<Post>) -> Unit
    ) {
        val post = posts.find { it.id == parentId }
        val currentlySaved = post?.isSaved == true
        val newPosts = posts.map { post ->
            if(post.id == parentId) {
                post.copy(isSaved = !post.isSaved)
            } else post
        }
        onStateUpdated(newPosts)
        val result = onRequest(currentlySaved)
        when(result) {
            is Resource.Success -> Unit
            is Resource.Error -> {
                val oldPosts = posts.map { post ->
                    if(post.id == parentId) {
                        post.copy(isSaved = currentlySaved)
                    } else post
                }
                onStateUpdated(oldPosts)
            }
        }
    }
}