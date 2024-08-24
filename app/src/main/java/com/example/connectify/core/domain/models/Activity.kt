package com.example.connectify.core.domain.models

import com.example.connectify.feature_activity.presentation.util.ActivityType

data class Activity(
    val id: String,
    val userId: String,
    val parentId: String,
    val username: String,
    val profilePictureUrl: String,
    val activityType: ActivityType,
    val formattedTime: String
)