package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.aarchangel.chatapp.data.TokenStorage // Assuming TokenStorage is in this path
import com.aarchangel.chatapp.dto.LoginRequest // DTO from the kotlin path
import com.aarchangel.chatapp.network.AuthService // Ktor AuthService from the kotlin path
import com.aarchangel.chatapp.network.NetworkResult // Ktor NetworkResult from the kotlin path
import com.aarchangel.chatapp.navigation.AppScreen // For navigation routes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the LoginScreen.
 */
data class LoginUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null
)

/**
 * ViewModel for the LoginScreen.
 * Handles UI state, validation, and login attempts.
 * // ChatApp by aarchangel
 */
class LoginViewModel(
    application: Application, // Added Application for context
    private val authService: AuthService, // Ktor AuthService
    private val tokenStorage: TokenStorage // TokenStorage
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    // Using AppScreen.EmailAuth.route as a placeholder for a dashboard/home screen route
    // You should define a proper route in AppScreen.kt like AppScreen.Dashboard.route
    private val _navigationEvent = MutableSharedFlow<String>() 
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    /** Updates the email/username in the UI state. */
    fun onEmailOrUsernameChanged(value: String) {
        _uiState.update { it.copy(emailOrUsername = value, loginError = null) }
    }

    /** Updates the password in the UI state. */
    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, loginError = null) }
    }

    /** Attempts to log in the user. */
    fun onLoginClicked() {
        val state = _uiState.value
        if (state.emailOrUsername.isBlank()) {
            _uiState.update { it.copy(loginError = "Email/Username cannot be empty.") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(loginError = "Password cannot be empty.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }
        
        viewModelScope.launch {
            val loginRequest = LoginRequest(emailOrUsername = state.emailOrUsername, password = state.password)
            when (val result = authService.login(loginRequest)) {
                is NetworkResult.Success -> {
                    tokenStorage.saveToken(result.data.accessToken)
                    _uiState.update { it.copy(isLoading = false, emailOrUsername = "", password = "") } // Clear both fields
                    _snackbarMessage.emit("Login successful!")
                    _navigationEvent.emit(AppScreen.EmailAuth.route) // TODO: Replace with actual Dashboard/Home route
                }
                is NetworkResult.Error.Unauthorized -> {
                    _uiState.update { it.copy(isLoading = false, loginError = result.message ?: "Invalid credentials.") }
                    _snackbarMessage.emit(result.message ?: "Invalid credentials.")
                }
                is NetworkResult.Error.BadRequest -> {
                    _uiState.update { it.copy(isLoading = false, loginError = result.message ?: "Invalid input.") }
                     _snackbarMessage.emit(result.message ?: "Invalid input.")
                }
                is NetworkResult.Error.ServerError -> {
                    _uiState.update { it.copy(isLoading = false, loginError = result.message ?: "Server error.") }
                    _snackbarMessage.emit(result.message ?: "Server error. Please try again later.")
                }
                is NetworkResult.Error.NetworkError -> {
                    _uiState.update { it.copy(isLoading = false, loginError = result.message ?: "Network connection failed.") }
                    _snackbarMessage.emit(result.message ?: "Network connection failed. Check your internet.")
                }
                is NetworkResult.Error.UnknownError -> {
                    _uiState.update { it.copy(isLoading = false, loginError = result.message ?: "An unknown error occurred.") }
                    _snackbarMessage.emit(result.message ?: "An unknown error occurred.")
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            authService: AuthService, // Ktor AuthService
            tokenStorage: TokenStorage,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null
        ): ViewModelProvider.Factory = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    return LoginViewModel(application, authService, tokenStorage) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
} 