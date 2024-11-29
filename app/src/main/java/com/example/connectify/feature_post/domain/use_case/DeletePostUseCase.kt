package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_post.domain.repository.PostRepository

class DeletePostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): SimpleResource {
        return repository.deletePost(postId)
    }
}