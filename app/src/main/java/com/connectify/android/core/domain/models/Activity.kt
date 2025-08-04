package com.connectify.android.core.domain.models

import com.connectify.android.feature_activity.presentation.util.ActivityType

data class Activity(
    val id: String,
    val userId: String,
    val parentId: String,
    val username: String,
    val profilePictureUrl: String,
    val activityType: ActivityType,
    val formattedTime: String
)