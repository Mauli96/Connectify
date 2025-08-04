package com.connectify.android.feature_post.presentation.create_post

import android.net.Uri

sealed interface CreatePostEvent {
    data class OnEnterDescription(val value: String): CreatePostEvent
    data class OnPickImage(val uri: Uri?): CreatePostEvent
    data class OnCropImage(val uri: Uri?): CreatePostEvent
    data object OnNavigatingToCrop: CreatePostEvent
    data object OnNavigatingToBackFromCrop: CreatePostEvent
    data object OnPostImage: CreatePostEvent
}