package com.example.connectify.feature_auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_auth.domain.use_case.AuthenticateUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        authenticateUser()
    }

    private fun authenticateUser() {
        viewModelScope.launch {
            val result = authenticateUseCase()
            when(result) {
                is Resource.Success -> {
                    _splashState.update {
                        it.copy(
                            isUserAuthenticated = true
                        )
                    }
                    _eventFlow.emit(
                        UiEvent.Navigate(Screen.MainFeedScreen.route)
                    )
                }
                is Resource.Error -> {
                    _splashState.update {
                        it.copy(
                            isUserAuthenticated = false
                        )
                    }
                    _eventFlow.emit(
                        UiEvent.Navigate(Screen.AuthScreen.route)
                    )
                }
            }
            _splashState.update {
                it.copy(
                    keepSplashScreenOn = false
                )
            }
        }
    }
}