package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.domain.repository.ProfileRepository

class LogoutUseCase(
    private val repository: ProfileRepository
) {

    operator fun invoke() {
        repository.logout()
    }
}