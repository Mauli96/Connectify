package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_post.domain.repository.PostRepository

class DeletePostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): SimpleResource {
        return repository.deletePost(postId)
    }
}