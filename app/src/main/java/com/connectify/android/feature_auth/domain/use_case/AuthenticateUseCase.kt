package com.connectify.android.feature_auth.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_auth.domain.repository.AuthRepository

class AuthenticateUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): SimpleResource {
        return repository.authenticate()
    }
}