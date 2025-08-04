package com.connectify.android.feature_auth.domain.use_case

import com.connectify.android.core.domain.util.ValidationUtil
import com.connectify.android.feature_auth.domain.models.ForgotPasswordResult
import com.connectify.android.feature_auth.domain.repository.AuthRepository

class ForgotPasswordUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): ForgotPasswordResult {
        val newPasswordError =  ValidationUtil.validatePassword(newPassword)
        val confirmPasswordError =  ValidationUtil.validateConfirmPassword(newPassword, confirmPassword)

        if(newPasswordError != null || confirmPasswordError != null) {
            return ForgotPasswordResult(
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        val result = repository.forgotPassword(
            email = email,
            newPassword = newPassword
        )

        return ForgotPasswordResult(
            result = result
        )
    }
}