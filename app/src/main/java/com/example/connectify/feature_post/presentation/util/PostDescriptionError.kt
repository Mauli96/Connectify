package com.example.connectify.feature_post.presentation.util

import com.example.connectify.core.util.Error
import com.example.connectify.feature_auth.presentation.util.AuthError

sealed class PostDescriptionError : Error() {
    object FieldEmpty : PostDescriptionError()
}
