package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>() // For future use (e.g., navigate to home)
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
        if (state.emailOrUsername.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(loginError = "Email/Username and password cannot be empty.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        // Placeholder for actual login logic
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Simulate network request
            _uiState.update { it.copy(isLoading = false) }
            // Mock success/failure
            if ((state.emailOrUsername == "test@example.com" || state.emailOrUsername == "testuser") && state.password == "password") {
                _snackbarMessage.emit("Login successful for ${state.emailOrUsername}")
                // TODO: Navigate to Home/Main screen
                // _navigationEvent.emit(AppScreen.HomeScreen.route) // Example
            } else {
                _snackbarMessage.emit("Login failed. Invalid credentials.")
                _uiState.update { it.copy(loginError = "Invalid email/username or password.") }
            }
        }
    }
} 