package com.example.connectify.feature_auth.domain.models

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_auth.presentation.util.AuthError

data class OtpResult(
    val emailError: AuthError? = null,
    val result: SimpleResource? = null
)