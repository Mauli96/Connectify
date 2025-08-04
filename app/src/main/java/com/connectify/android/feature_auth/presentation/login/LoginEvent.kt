package com.connectify.android.feature_auth.presentation.login

import com.connectify.android.feature_auth.domain.models.SignInResult

sealed interface LoginEvent {
    data class OnEnteredEmail(val email: String): LoginEvent
    data class OnEnteredPassword(val password: String): LoginEvent
    data class OnSignIn(val result: SignInResult): LoginEvent
    data object OnTogglePasswordVisibility: LoginEvent
    data object OnLogin: LoginEvent
}