package com.example.connectify.feature_auth.presentation.otp

import androidx.lifecycle.ViewModel
import com.example.connectify.core.domain.states.StandardTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val VALID_OTP_CODE = "1414"

@HiltViewModel
class OtpViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _emailState = MutableStateFlow(StandardTextFieldState())
    val emailState = _emailState.asStateFlow()

    fun onEvent(event: OtpEvent) {
        when(event) {
            is OtpEvent.EnteredEmail -> {
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
            is OtpEvent.OnVerifyEmail -> {
                _state.update {
                    it.copy(
                        showEmailInput = false
                    )
                }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if(currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        _state.update {
            it.copy(
                code = newCode,
                focusedIndex = if(wasNumberRemoved || it.code.getOrNull(index) != null) {
                    it.focusedIndex
                } else {
                    getNextFocusedTextFieldIndex(
                        currentCode = it.code,
                        currentFocusedIndex = it.focusedIndex
                    )
                },
                isValid = if(newCode.none { it == null }) {
                    newCode.joinToString("") == VALID_OTP_CODE
                } else null
            )
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