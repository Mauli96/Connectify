package com.example.connectify.feature_auth.domain.models

sealed interface SignInResult {
    data class Success(val email: String, val password: String): SignInResult
    data object Cancelled: SignInResult
    data object Failure: SignInResult
    data object NoCredentials: SignInResult
}