package com.connectify.android.feature_auth.data.remote

import com.connectify.android.core.data.dto.response.BasicApiResponse
import com.connectify.android.feature_auth.data.remote.request.CreateAccountRequest
import com.connectify.android.feature_auth.data.remote.request.EmailRequest
import com.connectify.android.feature_auth.data.remote.request.ForgotPasswordRequest
import com.connectify.android.feature_auth.data.remote.request.LoginRequest
import com.connectify.android.feature_auth.data.remote.request.OtpVerificationRequest
import com.connectify.android.feature_auth.data.remote.response.AuthResponse
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
        const val BASE_URL = "https://connectify-backend.fly.dev/"
    }
}