package com.connectify.android.feature_auth.presentation.otp

data class OtpState(
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = null,
    val showEmailInput: Boolean = true,
    val isValid: Boolean? = null,
    val isOtpGenerating: Boolean = false,
    val isOtpVerifying: Boolean = false
)