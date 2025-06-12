package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.NetworkResult
import com.aarchangel.chatapp.dto.LoginRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, List<String>>? = null
)

class LoginViewModel(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _loginEvent = Channel<Unit>()
    val loginEvent = _loginEvent.receiveAsFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            when (val result = authService.login(request)) {
                is NetworkResult.Success -> {
                    tokenStorage.saveToken(result.data.accessToken)
                    _loginState.value = LoginState()
                    _loginEvent.send(Unit)
                }
                is NetworkResult.Error -> {
                    _loginState.value = LoginState(
                        error = result.message,
                        fieldErrors = result.fieldErrors
                    )
                }
            }
        }
    }
} 