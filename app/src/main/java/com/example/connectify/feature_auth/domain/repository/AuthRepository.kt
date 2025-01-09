package com.example.connectify.feature_auth.domain.repository

import com.example.connectify.core.util.SimpleResource

interface AuthRepository {

    suspend fun register(
        email: String,
        username: String,
        password: String
    ): SimpleResource

    suspend fun login(
        email: String,
        password: String
    ): SimpleResource

    suspend fun generateOtp(
        email: String
    ): SimpleResource

    suspend fun verifyOtp(
        email: String,
        code: String
    ): SimpleResource

    suspend fun forgotPassword(
        email: String,
        newPassword: String
    ): SimpleResource

    suspend fun authenticate(): SimpleResource
}