package com.connectify.android.feature_auth.presentation.password

sealed interface PasswordEvent {
    data class OnEnteredNewPassword(val password: String): PasswordEvent
    data class OnConfirmPassword(val password: String): PasswordEvent
    data object OnToggleNewPasswordVisibility: PasswordEvent
    data object OnToggleConfirmPasswordVisibility: PasswordEvent
    data object OnPasswordChanged: PasswordEvent
}