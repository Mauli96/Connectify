package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_post.domain.repository.PostRepository

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