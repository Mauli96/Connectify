package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.util.Resource
import com.connectify.android.feature_post.domain.repository.PostRepository

class GetPostDetailsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String): Resource<Post> {
        return repository.getPostDetails(postId)
    }
}