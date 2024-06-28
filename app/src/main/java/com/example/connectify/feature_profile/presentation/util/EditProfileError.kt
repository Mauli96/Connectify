package com.example.connectify.feature_profile.presentation.util

import com.example.connectify.core.util.Error

sealed class EditProfileError : Error() {
    object FieldEmpty: EditProfileError()
}