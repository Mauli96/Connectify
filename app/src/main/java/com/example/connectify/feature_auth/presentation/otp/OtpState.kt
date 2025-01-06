package com.example.connectify.feature_auth.presentation.otp

data class OtpState(
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = null,
    val showEmailInput: Boolean = true,
    val isValid: Boolean? = null,
    val isLoading: Boolean = false
)