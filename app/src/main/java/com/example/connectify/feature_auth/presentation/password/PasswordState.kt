package com.example.connectify.feature_auth.presentation.password

data class PasswordState(
    val email: String = "",
    val isUpdatingPassword: Boolean = false
)
