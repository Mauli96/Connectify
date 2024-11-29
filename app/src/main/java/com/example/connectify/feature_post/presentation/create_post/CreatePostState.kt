package com.example.connectify.feature_post.presentation.create_post

import android.net.Uri

data class CreatePostState(
    val imageUri: Uri? = null,
    val isLoading: Boolean = false
)