package com.connectify.android.feature_activity.data.remote

import com.connectify.android.feature_activity.data.remote.response.ActivityDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ActivityApi {

    @GET("/api/activity/get")
    suspend fun getActivities(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<ActivityDto>

    companion object {
        const val BASE_URL = "https://connectify-backend.fly.dev/"
    }
}