package com.connectify.android.feature_auth.presentation.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.R
import com.connectify.android.core.domain.states.StandardTextFieldState
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.Screen
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_auth.domain.use_case.GenerateOtpUseCase
import com.connectify.android.feature_auth.domain.use_case.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val generateOtp: GenerateOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _emailState = MutableStateFlow(StandardTextFieldState())
    val emailState = _emailState.asStateFlow()

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: OtpEvent) {
        when(event) {
            is OtpEvent.OnEnteredEmail -> {
                _emailState.update {
                    it.copy(
                        text = event.email
                    )
                }
            }
            is OtpEvent.OnEnterNumber -> {
                enterNumber(event.number, event.index)
            }
            is OtpEvent.OnChangeFieldFocused -> {
                _state.update {
                    it.copy(
                        focusedIndex = event.index
                    )
                }
            }
            is OtpEvent.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update {
                    it.copy(
                        code = it.code.mapIndexed { index, number ->
                            if(index == previousIndex) {
                                null
                            } else {
                                number
                            }
                        },
                        focusedIndex = previousIndex
                    )
                }
            }
            is OtpEvent.OnSendOtp -> {
                sendOtp()
            }
        }
    }

    private fun sendOtp() {
        viewModelScope.launch {
            _emailState.update {
                it.copy(
                    error = null
                )
            }
            _state.update {
                it.copy(
                    isOtpGenerating = true
                )
            }
            val otpResult = generateOtp(email = emailState.value.text)

            if(otpResult.emailError != null) {
                _emailState.update {
                    it.copy(
                        error = otpResult.emailError
                    )
                }
            }

            when(otpResult.result) {
                is Resource.Success -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            UiText.StringResource(R.string.otp_generated)
                        )
                    )
                    _state.update {
                        it.copy(
                            showEmailInput = false,
                            isOtpGenerating = false
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = otpResult.result.uiText ?: UiText.unknownError()
                        )
                    )
                    _state.update {
                        it.copy(
                            isOtpGenerating = false
                        )
                    }
                }
                null -> {
                    _state.update {
                        it.copy(
                            isOtpGenerating = false
                        )
                    }
                }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if (currentIndex == index) number else currentNumber
        }
        val wasNumberRemoved = number == null

        viewModelScope.launch {
            _state.update {
                it.copy(
                    code = newCode,
                    focusedIndex = if (wasNumberRemoved || it.code.getOrNull(index) != null) {
                        it.focusedIndex
                    } else {
                        getNextFocusedTextFieldIndex(
                            currentCode = it.code,
                            currentFocusedIndex = it.focusedIndex
                        )
                    },
                    isValid = null
                )
            }

            val isValid = if (newCode.none { it == null }) {
                verifyCode(
                    email = emailState.value.text,
                    code = newCode.joinToString("")
                )
            } else null

            _state.update {
                it.copy(
                    isValid = isValid
                )
            }

            if(isValid == true) {
                delay(1000)
                _eventFlow.send(
                    UiEvent.Navigate(
                        route = Screen.PasswordScreen.route + "/${emailState.value.text}"
                    )
                )
            }
        }
    }

    private suspend fun verifyCode(
        email: String,
        code: String
    ): Boolean {
        _state.update {
            it.copy(
                isOtpVerifying = true
            )
        }
        val result = verifyOtp(email = email, code = code)

        return when(result) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isOtpVerifying = false
                    )
                }
                true
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isOtpVerifying = false
                    )
                }
                false
            }
        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if(currentFocusedIndex == null) {
            return null
        }

        if(currentFocusedIndex == 3) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if(index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if(number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}