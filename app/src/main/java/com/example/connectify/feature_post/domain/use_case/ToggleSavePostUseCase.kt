package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_post.domain.repository.PostRepository

class ToggleSavePostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        postId: String,
        isSaved: Boolean
    ): SimpleResource {
        return if(isSaved) {
            repository.removeSavedPost(postId)
        } else {
            repository.savePost(postId)
        }
    }
}