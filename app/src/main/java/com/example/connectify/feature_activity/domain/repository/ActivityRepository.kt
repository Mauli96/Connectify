package com.example.connectify.feature_activity.domain.repository

import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.util.Resource

interface ActivityRepository {

    suspend fun getActivities(
        page: Int,
        pageSize: Int
    ): Resource<List<Activity>>
}