package com.example.connectify.feature_auth.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun saveOnBoardingState(completed: Boolean)

    fun readOnBoardingState(): Flow<Boolean>
}