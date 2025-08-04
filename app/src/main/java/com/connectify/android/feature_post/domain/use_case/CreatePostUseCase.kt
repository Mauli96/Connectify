package com.connectify.android.feature_post.domain.use_case

import android.net.Uri
import com.connectify.android.R
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.SimpleResource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_post.domain.repository.PostRepository

class CreatePostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        description: String,
        imageUri: Uri?
    ): SimpleResource {
        if(imageUri == null) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_no_image_picked)
            )
        }
        if(description.isBlank()) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_description_blank)
            )
        }
        return repository.createPost(description, imageUri)
    }
}