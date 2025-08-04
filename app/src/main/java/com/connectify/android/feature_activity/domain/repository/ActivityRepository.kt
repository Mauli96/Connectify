package com.connectify.android.feature_activity.domain.repository

import com.connectify.android.core.domain.models.Activity
import com.connectify.android.core.util.Resource

interface ActivityRepository {

    suspend fun getActivities(
        page: Int,
        pageSize: Int
    ): Resource<List<Activity>>
}