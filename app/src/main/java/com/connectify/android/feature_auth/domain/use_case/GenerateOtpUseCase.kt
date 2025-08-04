package com.connectify.android.feature_auth.domain.use_case

import com.connectify.android.core.domain.util.ValidationUtil
import com.connectify.android.feature_auth.domain.models.OtpResult
import com.connectify.android.feature_auth.domain.repository.AuthRepository

class GenerateOtpUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String
    ): OtpResult {
        val emailError = ValidationUtil.validateEmail(email)

        if(emailError != null) {
            return OtpResult(
                emailError = emailError
            )
        }

        return OtpResult(
            result = repository.generateOtp(email.trim())
        )
    }
}