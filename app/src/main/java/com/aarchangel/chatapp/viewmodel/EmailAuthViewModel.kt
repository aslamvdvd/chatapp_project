package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log

/**
 * Data class to hold the UI state for email/password authentication.
 * // ChatApp by aarchangel
 */
data class EmailAuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val flowType: String = "login", // "login" or "signup"
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // New fields for signup
    val username: String = "",
    val firstName: String = "",
    val middleName: String = "", // Optional
    val lastName: String = "",
    val dateOfBirth: String = "", // Consider a Date object or proper validation later
    val gender: String = "", // Consider an enum or predefined list

    // Error states for new fields
    val usernameError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val dateOfBirthError: String? = null,
    val genderError: String? = null
)

/**
 * ViewModel for handling email and password authentication (both signup and login).
 * Manages UI state, validation, and navigation events for these flows.
 * // ChatApp by aarchangel
 */
class EmailAuthViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailAuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        val flowTypeFromNav: String? = savedStateHandle["flowType"]
        Log.d("EmailAuthViewModel", "Attempting to init. flowTypeFromNav from SavedStateHandle: '$flowTypeFromNav'")
        Log.d("EmailAuthViewModel", "Current uiState.flowType before update: '${_uiState.value.flowType}'")

        _uiState.update {
            it.copy(
                flowType = flowTypeFromNav ?: it.flowType
            )
        }
        Log.d("EmailAuthViewModel", "Updated uiState.flowType after update: '${_uiState.value.flowType}'")
    }

    /** Updates the email in the UI state. */
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    /** Updates the password in the UI state. */
    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    /** Updates the confirm password in the UI state. */
    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    /** Validates the email format. */
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /** Validates the password length (basic). */
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    // onEmailContinue() is removed as EmailEntryScreen is being removed.
    // Navigation goes directly to CreateAccountDetailsScreen from AuthOptions for signup.

    // --- New methods for signup details ---
    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun onFirstNameChanged(firstName: String) {
        _uiState.update { it.copy(firstName = firstName, firstNameError = null) }
    }

    fun onMiddleNameChanged(middleName: String) {
        _uiState.update { it.copy(middleName = middleName) } // No error for optional field
    }

    fun onLastNameChanged(lastName: String) {
        _uiState.update { it.copy(lastName = lastName, lastNameError = null) }
    }

    fun onDateOfBirthChanged(dob: String) {
        _uiState.update { it.copy(dateOfBirth = dob, dateOfBirthError = null) }
    }

    fun onGenderChanged(gender: String) {
        _uiState.update { it.copy(gender = gender, genderError = null) }
    }

    /** Called when "Continue" is clicked on CreateAccountDetailsScreen. */
    fun onCreateAccountDetailsContinue() {
        val state = _uiState.value
        var isValid = true

        // Validate Email
        if (!isEmailValid(state.email)) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }

        // Validate existing details
        if (state.username.isBlank()) {
            _uiState.update { it.copy(usernameError = "Username cannot be empty") }
            isValid = false
        }
        if (state.firstName.isBlank()) {
            _uiState.update { it.copy(firstNameError = "First name cannot be empty") }
            isValid = false
        }
        if (state.lastName.isBlank()) {
            _uiState.update { it.copy(lastNameError = "Last name cannot be empty") }
            isValid = false
        }
        if (state.dateOfBirth.isBlank()) { // Basic validation for now
            _uiState.update { it.copy(dateOfBirthError = "Date of birth cannot be empty") }
            isValid = false
        }
        // Gender validation is currently commented out as optional

        // Add password validation (since password fields will be on this screen)
        if (!isPasswordValid(state.password)) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }

        if (!isValid) return

        _uiState.update { it.copy(isLoading = true) }
        // Placeholder for actual account creation logic using all details including password
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Simulate network request
            _uiState.update { it.copy(isLoading = false) }

            if (isValid && state.username.isNotBlank() && state.email.isNotBlank() && state.password.isNotBlank()) { // check isValid flag too
                _snackbarMessage.emit("Account created successfully for ${state.email} with username ${state.username}!")
                // TODO: Navigate to a Home/Main screen upon successful auth
                // _navigationEvent.emit(AppScreen.HomeScreen.route) // Example
            } else {
                _snackbarMessage.emit("Account creation failed. Please check details and try again.")
            }
        }
    }

    /** Called when the final submit button is clicked on the PasswordEntryScreen. */
    fun onSubmitCredentials() {
        val state = _uiState.value
        var isValid = true

        // This method is now only relevant if PasswordEntryScreen is used for a login flow.
        // For signup, CreateAccountDetailsScreen and its continue method are used.

        if (!isPasswordValid(state.password)) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        if (!isValid) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Simulate network request
            _uiState.update { it.copy(isLoading = false) }

            // This path is now primarily for a login attempt via a dedicated PasswordEntryScreen (if used)
            if (state.flowType == "login") {
                if (state.email == "test@example.com" && state.password == "password") { // Mock success
                    _snackbarMessage.emit("Login successful for ${state.email}")
                    // TODO: Navigate to a Home/Main screen upon successful auth
                    // _navigationEvent.emit(AppScreen.HomeScreen.route) // Example
                } else {
                    _snackbarMessage.emit("Authentication failed. Please try again.")
                    _uiState.update { it.copy(passwordError = "Invalid credentials") }
                }
            } else if (state.flowType == "signup") {
                // This path in onSubmitCredentials for signup is now deprecated by the new flow.
                // CreateAccountDetailsScreen handles the final step of signup.
                _snackbarMessage.emit("Info: Signup via this (old password entry) path is deprecated.")
            }
        }
    }
} 