package com.example.connectify.feature_auth.data.remote.request

data class OtpVerificationRequest(
    val email: String,
    val code: String
)