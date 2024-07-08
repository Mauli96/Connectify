package com.example.connectify.core.domain.models

import com.example.connectify.feature_activity.presentation.util.ActivityAction

data class Activity(
    val username: String,
    val actionType: ActivityAction,
    val formattedTime: String,
)