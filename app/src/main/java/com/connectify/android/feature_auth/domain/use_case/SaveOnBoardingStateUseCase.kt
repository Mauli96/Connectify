package com.connectify.android.feature_auth.domain.use_case

import com.connectify.android.feature_auth.domain.repository.DataStoreRepository

class SaveOnBoardingStateUseCase(
    private val repository: DataStoreRepository
) {

    suspend operator fun invoke(
        completed: Boolean
    ) {
        return repository.saveOnBoardingState(completed)
    }
}