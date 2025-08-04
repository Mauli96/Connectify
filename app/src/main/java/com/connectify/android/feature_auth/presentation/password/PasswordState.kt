package com.connectify.android.feature_auth.presentation.password

data class PasswordState(
    val email: String = "",
    val isUpdatingPassword: Boolean = false
)
