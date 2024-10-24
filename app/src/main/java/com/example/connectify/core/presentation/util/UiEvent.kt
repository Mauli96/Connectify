package com.example.connectify.core.presentation.util

import com.example.connectify.core.util.UiText

sealed class UiEvent {
    data class ShowSnackbar(val uiText: UiText, val actionLabel: String? = null): UiEvent()
    data class Navigate(val route: String): UiEvent()
    object NavigateUp : UiEvent()
    object OnLogin: UiEvent()
}