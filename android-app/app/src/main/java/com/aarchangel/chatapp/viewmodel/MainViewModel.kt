package com.aarchangel.chatapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.dto.UserProfileDto
import com.aarchangel.chatapp.model.SessionState
import com.aarchangel.chatapp.network.AuthService
import com.aarchangel.chatapp.network.NetworkResult
import com.aarchangel.chatapp.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authService: AuthService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val token = authRepository.getToken()
            if (token == null) {
                _sessionState.value = SessionState.LoggedOut
                return@launch
            }

            when (val result = authService.getProfile(token)) {
                is NetworkResult.Success -> {
                    _sessionState.value = SessionState.LoggedIn(result.data)
                }
                is NetworkResult.Error -> {
                    authRepository.clearJwt()
                    _sessionState.value = SessionState.LoggedOut
                }
            }
        }
    }

    fun onLoginSuccess(userProfile: UserProfileDto) {
        _sessionState.value = SessionState.LoggedIn(userProfile)
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.clearJwt()
            _sessionState.value = SessionState.LoggedOut
        }
    }
} 