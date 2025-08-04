package com.connectify.android.feature_auth.data.remote.request

data class ForgotPasswordRequest(
    val email: String,
    val newPassword: String
)