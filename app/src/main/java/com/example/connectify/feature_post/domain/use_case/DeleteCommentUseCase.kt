package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_post.domain.repository.PostRepository

class DeleteCommentUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(commentId: String): SimpleResource {
        return repository.deleteComment(commentId)
    }
}