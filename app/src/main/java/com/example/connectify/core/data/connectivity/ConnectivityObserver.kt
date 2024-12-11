package com.example.connectify.core.data.connectivity

import com.example.connectify.core.domain.states.NetworkConnectionState
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val networkConnection: Flow<NetworkConnectionState>
}