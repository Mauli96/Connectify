package com.example.connectify.feature_auth.domain.use_case

import com.example.connectify.feature_auth.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class ReadOnBoardingStateUseCase(
    private val repository: DataStoreRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return repository.readOnBoardingState()
    }
}