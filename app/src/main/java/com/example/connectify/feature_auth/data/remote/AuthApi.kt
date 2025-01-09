package com.example.connectify.feature_auth.data.remote

import com.example.connectify.core.data.dto.response.BasicApiResponse
import com.example.connectify.feature_auth.data.remote.request.CreateAccountRequest
import com.example.connectify.feature_auth.data.remote.request.EmailRequest
import com.example.connectify.feature_auth.data.remote.request.ForgotPasswordRequest
import com.example.connectify.feature_auth.data.remote.request.LoginRequest
import com.example.connectify.feature_auth.data.remote.request.OtpVerificationRequest
import com.example.connectify.feature_auth.data.remote.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("/api/user/create")
    suspend fun register(
        @Body request: CreateAccountRequest
    ): BasicApiResponse<Unit>

    @POST("/api/user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BasicApiResponse<AuthResponse>

    @POST("/api/user/generate/otp")
    suspend fun generateOtp(
        @Body request: EmailRequest
    ): BasicApiResponse<Unit>

    @POST("/api/user/verify/otp")
    suspend fun verifyOtp(
        @Body request: OtpVerificationRequest
    ): BasicApiResponse<Unit>

    @PUT("/api/user/password/forgot")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): BasicApiResponse<Unit>

    @GET("/api/user/authenticate")
    suspend fun authenticate()

    companion object {
        const val BASE_URL = "http://192.168.0.100:8001/"
    }
}