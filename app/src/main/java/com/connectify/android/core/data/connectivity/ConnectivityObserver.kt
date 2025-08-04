package com.connectify.android.core.data.connectivity

import com.connectify.android.core.domain.states.NetworkConnectionState
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val networkConnection: Flow<NetworkConnectionState>
}