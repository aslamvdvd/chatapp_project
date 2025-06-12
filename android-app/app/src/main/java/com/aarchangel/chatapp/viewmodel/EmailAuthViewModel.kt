package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.dto.SignUpRequest
import com.aarchangel.chatapp.data.network.dto.ApiErrorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
            
            val apiDob = try {
                val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = displayFormat.parse(request.dateOfBirth)
                date?.let { apiFormat.format(it) } ?: ""
            } catch (e: Exception) {
                _signUpState.value = SignUpState(error = "Invalid date format. Use dd-MM-yyyy.")
                return@launch
            }

            val apiRequest = request.copy(dateOfBirth = apiDob)

            val result = authService.signUp(apiRequest)
            result.fold(
                onSuccess = {
                    _signUpState.value = SignUpState(isSuccess = true)
                },
                onFailure = { exception ->
                    val errorState = when (exception) {
                        is com.aarchangel.chatapp.data.network.ValidationException -> {
                            SignUpState(fieldErrors = exception.errorResponse.errors, error = exception.errorResponse.message)
                        }
                        is com.aarchangel.chatapp.data.network.ConflictException -> {
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