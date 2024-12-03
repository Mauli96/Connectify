package com.example.connectify.feature_profile.data.remote

import com.example.connectify.core.data.dto.response.BasicApiResponse
import com.example.connectify.core.data.dto.response.UserItemDto
import com.example.connectify.feature_profile.data.remote.request.FollowUpdateRequest
import com.example.connectify.feature_profile.data.remote.response.ProfileResponse
import com.example.connectify.feature_profile.data.remote.response.SkillDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {

    @GET("/api/user/profile")
    suspend fun getProfile(
        @Query("userId") userId: String
    ): BasicApiResponse<ProfileResponse>

    @Multipart
    @PUT("/api/user/update")
    suspend fun updateProfile(
        @Part bannerImage: MultipartBody.Part?,
        @Part profilePicture: MultipartBody.Part?,
        @Part updateProfileData: MultipartBody.Part
    ): BasicApiResponse<Unit>

    @GET("api/skills/get")
    suspend fun getSkills(): List<SkillDto>

    @GET("/api/user/search")
    suspend fun searchUser(
        @Query("query") query: String
    ): List<UserItemDto>

    @POST("/api/following/follow")
    suspend fun followUser(
        @Body request: FollowUpdateRequest
    ): BasicApiResponse<Unit>

    @DELETE("/api/following/unfollow")
    suspend fun unfollowUser(
        @Query("userId") userId: String
    ): BasicApiResponse<Unit>

    @GET("/api/get/following")
    suspend fun getFollowsByUser(
        @Query("userId") userId: String
    ): List<UserItemDto>

    @GET("/api/get/followers")
    suspend fun getFollowedToUser(
        @Query("userId") userId: String
    ): List<UserItemDto>

    @GET("/api/user/profile/picture")
    suspend fun getOwnProfilePicture(): BasicApiResponse<String>

    companion object {
        const val BASE_URL = "http://192.168.0.101:8001/"
    }
}