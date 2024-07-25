package com.example.connectify.feature_profile.presentation.search

import com.example.connectify.core.util.Error
import com.example.connectify.core.util.UiText

data class SearchError(
    val message: UiText
): Error()