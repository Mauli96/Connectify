package com.example.connectify.core.domain.use_case

import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository

class GetPostDownloadUrlUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): Resource<String> {
        return repository.getPostDownloadUrl(postId)
    }
}