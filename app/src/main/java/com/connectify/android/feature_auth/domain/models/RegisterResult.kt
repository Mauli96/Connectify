package com.connectify.android.feature_auth.domain.models

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_auth.presentation.util.AuthError

data class RegisterResult(
    val emailError: AuthError? = null,
    val usernameError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null
)