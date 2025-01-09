package com.example.connectify.feature_auth.presentation.password

sealed interface PasswordEvent {
    data class EnteredPassword1(val password: String): PasswordEvent
    data class EnteredPassword2(val password: String): PasswordEvent
    data object TogglePasswordVisibility1: PasswordEvent
    data object TogglePasswordVisibility2: PasswordEvent
    data object OnPasswordChanged: PasswordEvent
}