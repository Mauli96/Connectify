package com.connectify.android.feature_profile.presentation.util

import com.connectify.android.core.util.Error

sealed class EditProfileError : Error() {
    data object FieldEmpty: EditProfileError()
}