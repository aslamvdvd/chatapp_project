package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.data.local.PreferenceManager
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.NetworkResult
import com.aarchangel.chatapp.model.SessionState
import com.aarchangel.chatapp.model.dto.UserProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authService: AuthService,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    val hasAgreedToTerms: StateFlow<Boolean> = preferenceManager.hasAgreedToTerms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

            when (val result = authService.getProfile()) {
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

    fun onTermsAgreed() {
        viewModelScope.launch {
            preferenceManager.setTermsAgreement(true)
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