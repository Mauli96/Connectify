package com.connectify.android.feature_auth.presentation.register

sealed interface RegisterEvent {
    data class OnEnteredUsername(val value: String): RegisterEvent
    data class OnEnteredEmail(val value: String): RegisterEvent
    data class OnEnteredPassword(val value: String): RegisterEvent
    data object OnTogglePasswordVisibility : RegisterEvent
    data object OnRegister : RegisterEvent
}