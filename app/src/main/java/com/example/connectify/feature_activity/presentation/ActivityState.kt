package com.example.connectify.feature_activity.presentation

import com.example.connectify.core.domain.models.Activity

data class ActivityState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false,
)