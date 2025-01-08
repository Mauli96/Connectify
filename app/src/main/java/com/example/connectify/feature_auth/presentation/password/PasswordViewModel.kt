package com.example.connectify.feature_auth.presentation.password

import androidx.lifecycle.ViewModel
import com.example.connectify.core.domain.states.PasswordTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _passwordState1 = MutableStateFlow(PasswordTextFieldState())
    val passwordState1 = _passwordState1.asStateFlow()

    private val _passwordState2 = MutableStateFlow(PasswordTextFieldState())
    val passwordState2 = _passwordState2.asStateFlow()

    fun onEvent(event: PasswordEvent) {
        when(event) {
            is PasswordEvent.EnteredPassword1 -> {
                _passwordState1.update {
                    it.copy(
                        text = event.password
                    )
                }
            }
            is PasswordEvent.EnteredPassword2 -> {
                _passwordState2.update {
                    it.copy(
                        text = event.password
                    )
                }
            }
            is PasswordEvent.TogglePasswordVisibility1 -> {
                _passwordState1.update {
                    it.copy(
                        isPasswordVisible = !passwordState1.value.isPasswordVisible
                    )
                }
            }
            is PasswordEvent.TogglePasswordVisibility2 -> {
                _passwordState2.update {
                    it.copy(
                        isPasswordVisible = !passwordState2.value.isPasswordVisible
                    )
                }
            }
        }
    }
}