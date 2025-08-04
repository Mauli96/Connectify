package com.connectify.android.feature_activity.presentation

import com.connectify.android.core.domain.models.Activity

data class ActivityState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false,
)