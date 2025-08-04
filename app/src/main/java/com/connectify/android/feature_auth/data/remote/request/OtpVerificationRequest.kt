package com.connectify.android.feature_auth.data.remote.request

data class OtpVerificationRequest(
    val email: String,
    val code: String
)