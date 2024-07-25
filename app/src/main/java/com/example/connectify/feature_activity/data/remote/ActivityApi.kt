package com.example.connectify.feature_activity.data.remote

import com.example.connectify.core.util.Constants
import com.example.connectify.feature_activity.data.remote.response.ActivityDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ActivityApi {

    @GET("/api/activity/get")
    suspend fun getActivities(
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<ActivityDto>

    companion object {
        const val BASE_URL = "http://192.168.0.209:8001/"
    }
}