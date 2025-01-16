package com.example.connectify.core.presentation.util

import com.example.connectify.core.util.UiText

sealed interface UiEvent {
    data class ShowSnackbar(val uiText: UiText, val actionLabel: String? = null) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateUp : UiEvent
    data object OnRegister : UiEvent
    data object OnLogin : UiEvent
}