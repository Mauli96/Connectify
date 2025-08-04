package com.connectify.android.core.domain.states

import com.connectify.android.core.util.Error

data class StandardTextFieldState(
    val text: String = "",
    val error: Error? = null
)