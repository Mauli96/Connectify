package com.connectify.android.feature_post.presentation.util

import com.connectify.android.core.util.Error

sealed class PostDescriptionError : Error() {
    object FieldEmpty : PostDescriptionError()
}
