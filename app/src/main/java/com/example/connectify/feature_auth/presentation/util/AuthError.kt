package com.example.connectify.feature_auth.presentation.util

import com.example.connectify.core.util.Error

sealed class AuthError : Error() {
    object FieldEmpty : AuthError()
    object InputTooShort : AuthError()
    object InvalidEmail : AuthError()
    object InvalidPassword : AuthError()
    object PasswordsDoNotMatch : AuthError()
}