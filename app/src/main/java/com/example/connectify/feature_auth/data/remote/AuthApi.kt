package com.example.connectify.feature_auth.data.remote

import com.example.connectify.core.data.dto.response.BasicApiResponse
import com.example.connectify.feature_auth.data.remote.request.CreateAccountRequest
import com.example.connectify.feature_auth.data.remote.request.LoginRequest
import com.example.connectify.feature_auth.data.remote.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/user/create")
    suspend fun register(
        @Body request: CreateAccountRequest
    ): BasicApiResponse<Unit>

    @POST("/api/user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BasicApiResponse<AuthResponse>

    @GET("/api/user/authenticate")
    suspend fun authenticate()

    companion object {
        const val BASE_URL = "http://192.168.0.103:8001/"
    }
}