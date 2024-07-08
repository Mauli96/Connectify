package com.example.connectify.feature_auth.domain.models

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_auth.presentation.util.AuthError

data class RegisterResult(
    val emailError: AuthError? = null,
    val usernameError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null
)