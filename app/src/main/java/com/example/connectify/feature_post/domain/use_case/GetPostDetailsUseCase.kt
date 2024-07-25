package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository

class GetPostDetailsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): Resource<Post> {
        return repository.getPostDetails(postId)
    }
}