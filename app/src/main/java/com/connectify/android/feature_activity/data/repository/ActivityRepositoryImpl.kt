package com.connectify.android.feature_activity.data.repository

import com.connectify.android.R
import com.connectify.android.core.domain.models.Activity
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_activity.data.remote.ActivityApi
import com.connectify.android.feature_activity.domain.repository.ActivityRepository
import retrofit2.HttpException
import java.io.IOException

class ActivityRepositoryImpl(
    private val api: ActivityApi
): ActivityRepository {

    override suspend fun getActivities(
        page: Int,
        pageSize: Int
    ): Resource<List<Activity>> {
        return try {
            val activities = api.getActivities(
                page = page,
                pageSize = pageSize
            ).map { it.toActivity() }
            Resource.Success(data = activities)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }
}