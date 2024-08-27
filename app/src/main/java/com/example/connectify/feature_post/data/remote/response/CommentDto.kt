package com.example.connectify.feature_post.data.remote.response

import com.example.connectify.core.domain.models.Comment
import com.example.connectify.feature_activity.presentation.util.DateFormatUtil
import java.text.SimpleDateFormat
import java.util.Locale

data class CommentDto(
    val id: String,
    val username: String,
    val profilePictureUrl: String,
    val timestamp: Long,
    val comment: String,
    val isLiked: Boolean,
    val likeCount: Int,
    val isOwnComment: Boolean
) {
    fun toComment(): Comment {
        return Comment(
            id = id,
            username = username,
            profilePictureUrl = profilePictureUrl,
            formattedTime = DateFormatUtil.timestampToFormattedString(timestamp,"MMM dd, hh:mm a"),
            comment = comment,
            isLiked = isLiked,
            likeCount = likeCount,
            isOwnComment = isOwnComment
        )
    }
}