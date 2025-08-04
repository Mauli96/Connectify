package com.connectify.android.feature_auth.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_auth.domain.repository.AuthRepository

class VerifyOtpUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        code: String
    ): SimpleResource {
        return repository.verifyOtp(email, code)
    }
}