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
        val emailFromNav: String? = savedStateHandle["email"] // Will be present when coming to PasswordEntryScreen
        // Or if we decide to pass it to CreateAccountDetailsScreen too
        _uiState.update {
            it.copy(
                flowType = flowTypeFromNav ?: it.flowType, // Keep existing if not from nav (e.g. direct Login screen)
                email = emailFromNav ?: it.email
            )
        }
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

    /** Called when the "Continue" button is clicked on the EmailEntryScreen. */
    fun onEmailContinue() {
        val currentFlowType = _uiState.value.flowType
        val email = _uiState.value.email

        if (!isEmailValid(email)) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            return
        }

        viewModelScope.launch {
            if (currentFlowType == "signup") {
                _navigationEvent.emit(AppScreen.CreateAccountDetails.createRoute(email, currentFlowType))
            } else { // Should not happen if onEmailContinue is only for signup from EmailEntry
                // This case needs to be re-evaluated. Original was direct to PasswordEntry.
                // For now, let's assume EmailEntryScreen using this method is always part of signup for this new flow.
                // Or, if Login flow also uses EmailEntry first, then it would go to PasswordEntry or a combined Login screen.
                // Given new requirements, Login flow will have its own dedicated LoginScreen.
                // So, EmailEntryScreen is now primarily for the first step of SIGNUP.
                _snackbarMessage.emit("Error: Unexpected flow type in onEmailContinue")
            }
        }
    }

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
        if (state.gender.isBlank()) {
            _uiState.update { it.copy(genderError = "Gender cannot be empty") }
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            _navigationEvent.emit(AppScreen.PasswordEntry.createRoute(state.flowType, state.email))
        }
    }

    /** Called when the final submit button is clicked on the PasswordEntryScreen (for signup). */
    fun onSubmitCredentials() {
        val state = _uiState.value
        var isValid = true

        if (!isPasswordValid(state.password)) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        if (state.flowType == "signup" && state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }

        if (!isValid) return

        _uiState.update { it.copy(isLoading = true) }
        // Placeholder for actual authentication logic
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Simulate network request
            _uiState.update { it.copy(isLoading = false) }
            if (state.email == "test@example.com" && state.password == "password") { // Mock success
                _snackbarMessage.emit("${state.flowType.capitalize()} successful for ${state.email}")
                // TODO: Navigate to a Home/Main screen upon successful auth
                // _navigationEvent.emit(AppScreen.HomeScreen.route) // Example
            } else {
                _snackbarMessage.emit("Authentication failed. Please try again.")
                _uiState.update { it.copy(passwordError = "Invalid credentials") } // Generic error on password for failed login
            }
        }
    }
} 