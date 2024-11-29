package com.example.connectify.feature_activity.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateFormatUtil {

    fun timestampToFormattedString(
        timestamp: Long,
        pattern: String
    ): String {
        val dateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return DateTimeFormatter.ofPattern(pattern).format(dateTime)
    }
}