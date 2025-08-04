package com.connectify.android.feature_auth.domain.models

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_auth.presentation.util.AuthError

data class LoginResult(
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null,
    val result: SimpleResource? = null
)