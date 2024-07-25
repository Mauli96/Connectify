package com.example.connectify.feature_activity.domain.repository

import androidx.paging.PagingData
import com.example.connectify.core.domain.models.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {

    val activities: Flow<PagingData<Activity>>
}