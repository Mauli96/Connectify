package com.example.connectify.feature_post.presentation.util

import com.example.connectify.core.util.Error

sealed class CommentError : Error() {
    object FieldEmpty: CommentError()
}