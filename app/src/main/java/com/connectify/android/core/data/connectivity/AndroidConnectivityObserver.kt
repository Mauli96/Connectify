package com.connectify.android.core.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.connectify.android.core.domain.states.NetworkConnectionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidConnectivityObserver(
    private val context: Context
): ConnectivityObserver {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    override val networkConnection: Flow<NetworkConnectionState>
        get() = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    trySend(
                        if(connected) NetworkConnectionState.Available
                        else NetworkConnectionState.Unavailable
                    )
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(NetworkConnectionState.Unavailable)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(NetworkConnectionState.Unavailable)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkConnectionState.Available)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
}