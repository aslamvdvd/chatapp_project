package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.ConflictException
import com.aarchangel.chatapp.data.network.ValidationException
import com.aarchangel.chatapp.model.dto.SignUpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, List<String>>? = null
)

class EmailAuthViewModel(private val authService: AuthService) : ViewModel() {

    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUp(request: SignUpRequest) {
        viewModelScope.launch {
            _signUpState.value = SignUpState(isLoading = true)
            val result = authService.signUp(request)
            result.fold(
                onSuccess = {
                    _signUpState.value = SignUpState(isSuccess = true)
                },
                onFailure = { exception ->
                    val errorState = when (exception) {
                        is ValidationException -> {
                            SignUpState(fieldErrors = exception.errorResponse.errors, error = exception.errorResponse.message)
                        }
                        is ConflictException -> {
                            SignUpState(error = exception.message)
                        }
                        else -> SignUpState(error = "An unexpected error occurred: ${exception.message}")
                    }
                    _signUpState.value = errorState
                }
            )
        }
    }
} 