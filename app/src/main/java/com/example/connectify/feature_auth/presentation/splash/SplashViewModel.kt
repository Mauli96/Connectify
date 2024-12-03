package com.example.connectify.feature_auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_auth.domain.use_case.AuthenticateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase
) : ViewModel() {

    private val _keepSplashScreenOn = MutableStateFlow(true)
    val keepSplashScreenOn = _keepSplashScreenOn.asStateFlow()

    private val _isUserAuthenticated = MutableStateFlow<Boolean?>(null)
    val isUserAuthenticated = _isUserAuthenticated
        .onStart { authenticateUser() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun authenticateUser() {
        viewModelScope.launch {
            val result = authenticateUseCase()
            when(result) {
                is Resource.Success -> {
                    _isUserAuthenticated.value = true
                    _eventFlow.emit(
                        UiEvent.Navigate(Screen.MainFeedScreen.route)
                    )
                }
                is Resource.Error -> {
                    _isUserAuthenticated.value = false
                    _eventFlow.emit(
                        UiEvent.Navigate(Screen.LoginScreen.route)
                    )
                }
            }
            _keepSplashScreenOn.value = false
        }
    }
}