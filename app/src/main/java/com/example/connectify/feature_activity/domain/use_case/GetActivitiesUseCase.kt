package com.example.connectify.feature_activity.domain.use_case

import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_activity.domain.repository.ActivityRepository

class GetActivitiesUseCase(
    private val repository: ActivityRepository
) {

    suspend operator fun invoke(
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Activity>> {
        return repository.getActivities(page, pageSize)
    }
}