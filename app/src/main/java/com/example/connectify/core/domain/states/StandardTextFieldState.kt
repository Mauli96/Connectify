package com.example.connectify.core.domain.states

import com.example.connectify.core.util.Error

data class StandardTextFieldState(
    val text: String = "",
    val error: Error? = null
)