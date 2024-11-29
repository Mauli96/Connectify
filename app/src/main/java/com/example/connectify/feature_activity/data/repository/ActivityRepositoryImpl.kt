package com.example.connectify.feature_activity.data.repository

import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_activity.data.remote.ActivityApi
import com.example.connectify.feature_activity.domain.repository.ActivityRepository
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