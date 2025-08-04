package com.connectify.android.feature_auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.Screen
import com.connectify.android.feature_auth.domain.use_case.AuthenticateUseCase
import com.connectify.android.feature_auth.domain.use_case.ReadOnBoardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val readOnBoardingState: ReadOnBoardingStateUseCase,
    private val authenticateUseCase: AuthenticateUseCase
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState = _splashState.asStateFlow()

    init {
        initializeSplashState()
    }

    private fun initializeSplashState() {
        viewModelScope.launch {
            _splashState.update {
                it.copy(
                    keepSplashScreenOn = true
                )
            }

            val hasCompletedOnboarding = readOnBoardingState().first()
            if(!hasCompletedOnboarding) {
                navigateToScreen(Screen.OnBoardingScreen.route)
            } else {
                authenticateUser()
            }
        }
    }

    private suspend fun authenticateUser() {
        val result = authenticateUseCase()
        when(result) {
            is Resource.Success -> {
                navigateToScreen(Screen.MainFeedScreen.route)
            }
            is Resource.Error -> {
                navigateToScreen(Screen.AuthScreen.route)
            }
        }
    }

    private fun navigateToScreen(destination: String) {
        _splashState.update {
            it.copy(
                startDestination = destination,
                keepSplashScreenOn = false
            )
        }
    }
}