package com.connectify.android.feature_post.presentation.create_post

import android.net.Uri

data class CreatePostState(
    val imageUri: Uri? = null,
    val isPosting: Boolean = false
)