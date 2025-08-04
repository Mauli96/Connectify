package com.connectify.android.feature_activity.domain.use_case

import com.connectify.android.core.domain.models.Activity
import com.connectify.android.core.util.Constants
import com.connectify.android.core.util.Resource
import com.connectify.android.feature_activity.domain.repository.ActivityRepository

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