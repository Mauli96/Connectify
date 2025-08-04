package com.connectify.android.core.domain.use_case

import com.connectify.android.core.util.Resource
import com.connectify.android.feature_post.domain.repository.PostRepository

class GetPostDownloadUrlUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): Resource<String> {
        return repository.getPostDownloadUrl(postId)
    }
}