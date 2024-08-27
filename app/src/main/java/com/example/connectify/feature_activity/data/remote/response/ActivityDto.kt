package com.example.connectify.feature_activity.data.remote.response

import com.example.connectify.core.domain.models.Activity
import com.example.connectify.feature_activity.presentation.util.ActivityType
import com.example.connectify.feature_activity.presentation.util.DateFormatUtil
import java.text.SimpleDateFormat
import java.util.*

data class ActivityDto(
    val timestamp: Long,
    val userId: String,
    val profilePictureUrl: String,
    val parentId: String,
    val type: Int,
    val username: String,
    val id: String
) {
    fun toActivity(): Activity {
        return Activity(
            id = id,
            userId = userId,
            parentId = parentId,
            username = username,
            profilePictureUrl = profilePictureUrl,
            activityType = when(type) {
                ActivityType.FollowedUser.type -> ActivityType.FollowedUser
                ActivityType.LikedPost.type -> ActivityType.LikedPost
                ActivityType.LikedComment.type -> ActivityType.LikedComment
                ActivityType.CommentedOnPost.type -> ActivityType.CommentedOnPost
                else -> ActivityType.FollowedUser
            },
            formattedTime = DateFormatUtil.timestampToFormattedString(timestamp, "MMM dd, hh:mm a")
        )
    }
}