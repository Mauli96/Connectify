package com.example.connectify.feature_auth.domain.models

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_auth.presentation.util.AuthError

data class ForgotPasswordResult(
    val newPasswordError: AuthError? = null,
    val confirmPasswordError: AuthError? = null,
    val result: SimpleResource? = null
)
