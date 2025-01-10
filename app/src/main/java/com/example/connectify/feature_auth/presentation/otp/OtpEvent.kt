package com.example.connectify.feature_auth.presentation.otp

sealed interface OtpEvent {
    data class OnEnteredEmail(val email: String): OtpEvent
    data class OnEnterNumber(val number: Int?, val index: Int): OtpEvent
    data class OnChangeFieldFocused(val index: Int): OtpEvent
    data object OnKeyboardBack: OtpEvent
    data object OnSendOtp: OtpEvent
}