package com.connectify.android.core.util

import com.connectify.android.core.domain.models.Post

class DefaultPostLiker : PostLiker {

    override suspend fun toggleLike(
        posts: List<Post>,
        parentId: String,
        onRequest: suspend (isLiked: Boolean) -> SimpleResource,
        onStateUpdated: (List<Post>) -> Unit
    ) {
        val post = posts.find { it.id == parentId }
        val currentlyLiked = post?.isLiked == true
        val currentLikeCount = post?.likeCount ?: 0
        val newPosts = posts.map { post ->
            if(post.id == parentId) {
                post.copy(
                    isLiked = !post.isLiked,
                    likeCount = if(currentlyLiked) {
                        post.likeCount.minus(1)
                    } else post.likeCount.plus(1)
                )
            } else post
        }
        onStateUpdated(newPosts)
        val result = onRequest(currentlyLiked)
        when(result) {
            is Resource.Success -> Unit
            is Resource.Error -> {
                val oldPosts = posts.map { post ->
                    if(post.id == parentId) {
                        post.copy(
                            isLiked = currentlyLiked,
                            likeCount = currentLikeCount
                        )
                    } else post
                }
                onStateUpdated(oldPosts)
            }
        }
    }
}