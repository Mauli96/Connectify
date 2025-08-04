package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.domain.repository.ProfileRepository

class LogoutUseCase(
    private val repository: ProfileRepository
) {

    operator fun invoke() {
        repository.logout()
    }
}