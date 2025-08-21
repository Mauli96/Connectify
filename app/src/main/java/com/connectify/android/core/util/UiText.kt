package com.connectify.android.core.util

import androidx.annotation.StringRes
import com.connectify.android.R

sealed class UiText {
    data class DynamicString(val value: String): UiText()
    data class StringResource(@param:StringRes val id: Int): UiText()

    companion object {
        fun unknownError(): UiText {
            return StringResource(R.string.error_unknown)
        }
    }
}