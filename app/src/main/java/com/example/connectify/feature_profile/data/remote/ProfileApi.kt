package com.example.connectify.feature_profile.data.remote

import com.example.connectify.core.data.dto.response.BasicApiResponse
import com.example.connectify.feature_profile.data.remote.response.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {

    @GET("/api/user/profile")
    suspend fun getProfile(
        @Query("userId") userId: String
    ): BasicApiResponse<ProfileResponse>

    companion object {
        const val BASE_URL = "http://192.168.0.210:8001/"
    }
}