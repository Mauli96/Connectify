package com.example.connectify.feature_auth.presentation.login

import com.example.connectify.feature_auth.domain.models.SignInResult

sealed class LoginEvent {
    data class EnteredEmail(val email: String): LoginEvent()
    data class EnteredPassword(val password: String): LoginEvent()
    data class OnSignIn(val result: SignInResult): LoginEvent()
    object TogglePasswordVisibility: LoginEvent()
    object Login: LoginEvent()
}