package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_post.domain.repository.PostRepository

class DeleteCommentUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(commentId: String): SimpleResource {
        return repository.deleteComment(commentId)
    }
}