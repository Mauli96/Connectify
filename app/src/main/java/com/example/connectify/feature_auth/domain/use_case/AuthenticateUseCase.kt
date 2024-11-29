package com.example.connectify.feature_auth.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_auth.domain.repository.AuthRepository

class AuthenticateUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): SimpleResource {
        return repository.authenticate()
    }
}