package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.data.network.ConflictException
import com.aarchangel.chatapp.data.network.ValidationException
import com.aarchangel.chatapp.data.network.dto.SignUpRequest
import com.aarchangel.chatapp.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle

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
 * ViewModel for handling email and password authentication (primarily signup now).
 * Manages UI state, validation, and navigation events for these flows.
 * // ChatApp by aarchangel
 */
class EmailAuthViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authService: AuthService = AuthServiceImpl() // Instantiate AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailAuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // SDF for parsing UI date and formatting for API
    private val uiDateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val apiDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        val flowTypeFromNav: String? = savedStateHandle["flowType"]
        val emailFromNav: String? = savedStateHandle["email"]
        Log.d("EmailAuthViewModel", "Init - flowTypeFromNav: '$flowTypeFromNav', emailFromNav: '$emailFromNav'")
        _uiState.update {
            it.copy(
                flowType = flowTypeFromNav ?: it.flowType,
                email = emailFromNav ?: it.email
            )
        }
        Log.d("EmailAuthViewModel", "Updated uiState - flowType: '${_uiState.value.flowType}', email: '${_uiState.value.email}'")
    }

    /** Clears all fields and errors related to the sign-up form. */
    fun clearSignUpForm() {
        _uiState.update { currentState ->
            currentState.copy(
                email = "",
                password = "",
                confirmPassword = "",
                username = "",
                firstName = "",
                middleName = "",
                lastName = "",
                dateOfBirth = "",
                gender = "",
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                usernameError = null,
                firstNameError = null,
                lastNameError = null,
                dateOfBirthError = null,
                genderError = null,
                isLoading = false
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
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() }
    }

    // --- Methods for signup details ---
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

    /** Updates date of birth from Date Picker. Expects "DD-MM-YYYY" format. */
    fun onDateOfBirthChanged(dob: String) {
        _uiState.update { it.copy(dateOfBirth = dob, dateOfBirthError = null) }
    }

    /** 
     * Updates date of birth from manual text input.
     * The input string is already formatted by DateVisualTransformation.
     */
    fun onDateOfBirthManuallyChanged(formattedInput: String) {
        // The input is already formatted by DateVisualTransformation (e.g., "12-03-1990" or "12-03-")
        // We just need to update the state with this formatted string.
        // The validation in onSignUpAttempt will parse this DD-MM-YYYY string.
        _uiState.update { it.copy(dateOfBirth = formattedInput, dateOfBirthError = null) }
    }

    fun onGenderChanged(gender: String) {
        _uiState.update { it.copy(gender = gender, genderError = null) }
    }

    /** Called when the "Sign Up" button is clicked on the consolidated signup screen. */
    fun onSignUpAttempt() {
        val state = _uiState.value
        var validationPassed = true

        // Clear previous errors
        _uiState.update { it.copy(isLoading = false, emailError = null, usernameError = null, firstNameError = null, lastNameError = null, dateOfBirthError = null, genderError = null, passwordError = null, confirmPasswordError = null) }

        if (!isEmailValid(state.email)) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            validationPassed = false
        }
        if (state.username.isBlank()) {
            _uiState.update { it.copy(usernameError = "Username cannot be empty") }
            validationPassed = false
        }
        if (state.firstName.isBlank()) {
            _uiState.update { it.copy(firstNameError = "First name cannot be empty") }
            validationPassed = false
        }
        if (state.lastName.isBlank()) {
            _uiState.update { it.copy(lastNameError = "Last name cannot be empty") }
            validationPassed = false
        }

        var apiDob: String? = null
        if (state.dateOfBirth.isBlank()) {
            _uiState.update { it.copy(dateOfBirthError = "Date of birth cannot be empty") }
            validationPassed = false
        } else {
            try {
                val parsedDate = uiDateFormatter.parse(state.dateOfBirth)
                if (parsedDate != null) {
                    apiDob = apiDateFormatter.format(parsedDate)
                    // Age validation (COPPA)
                    val today = Calendar.getInstance()
                    val dobCalendar = Calendar.getInstance().apply { time = parsedDate }
                    var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
                    if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    if (age < 13) {
                        _uiState.update { it.copy(dateOfBirthError = "You must be at least 13 years old.") }
                        validationPassed = false
                    }
                } else {
                    _uiState.update { it.copy(dateOfBirthError = "Invalid date format.") }
                    validationPassed = false
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(dateOfBirthError = "Invalid date format.") }
                validationPassed = false
                Log.e("EmailAuthViewModel", "Date parsing/formatting error: ", e)
            }
        }

        if (state.gender.isBlank()) { // Example: Making gender mandatory
            // _uiState.update { it.copy(genderError = "Gender cannot be empty") }
            // validationPassed = false
            // For now, it's optional as per backend spec
        }

        if (!isPasswordValid(state.password)) {
            _uiState.update { it.copy(passwordError = "Password: min 8 chars, letters, numbers, special chars.") }
            validationPassed = false
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            validationPassed = false
        }

        if (!validationPassed || apiDob == null) return

        _uiState.update { it.copy(isLoading = true) }

        val signUpRequest = SignUpRequest(
            email = state.email,
            username = state.username,
            firstName = state.firstName,
            middleName = state.middleName.takeIf { it.isNotBlank() },
            lastName = state.lastName,
            dateOfBirth = apiDob, // Use YYYY-MM-DD formatted date
            gender = state.gender.takeIf { it.isNotBlank() },
            password = state.password,
            confirmPassword = state.confirmPassword
        )

        viewModelScope.launch {
            val result = authService.signUp(signUpRequest)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _snackbarMessage.emit("Account created successfully! Please log in.")
                    clearSignUpForm() 
                    // Optionally navigate to login screen: _navigationEvent.emit(AppScreen.LoginScreen.route)
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    when (exception) {
                        is ValidationException -> {
                            _snackbarMessage.emit(exception.errorResponse.message) // General validation message
                            exception.errorResponse.errors?.forEach { (field, messages) ->
                                val message = messages.firstOrNull() ?: "Validation Error"
                                when (field) {
                                    "email" -> _uiState.update { it.copy(emailError = message) }
                                    "username" -> _uiState.update { it.copy(usernameError = message) }
                                    "first_name" -> _uiState.update { it.copy(firstNameError = message) }
                                    "last_name" -> _uiState.update { it.copy(lastNameError = message) }
                                    "date_of_birth" -> _uiState.update { it.copy(dateOfBirthError = message) }
                                    "gender" -> _uiState.update { it.copy(genderError = message) }
                                    "password" -> _uiState.update { it.copy(passwordError = message) }
                                    "confirm_password" -> _uiState.update { it.copy(confirmPasswordError = message) }
                                    // Add other fields as necessary
                                }
                            }
                        }
                        is ConflictException -> {
                            _snackbarMessage.emit(exception.message ?: "An email or username conflict occurred.")
                            if (exception.message?.contains("email", ignoreCase = true) == true) {
                                _uiState.update { it.copy(emailError = exception.message) }
                            } else if (exception.message?.contains("username", ignoreCase = true) == true) {
                                _uiState.update { it.copy(usernameError = exception.message) }
                            }
                        }
                        else -> {
                            _snackbarMessage.emit("Sign up failed: ${exception.message ?: "Unknown error"}")
                            Log.e("EmailAuthViewModel", "SignUp failed", exception)
                        }
                    }
                }
            )
        }
    }

    /** 
     * This method might still be used if there's a separate login flow that 
     * specifically uses an email/password entry screen separate from the main LoginScreen.
     * For the consolidated signup, onSignUpAttempt() is used.
     */
    fun onSubmitCredentials() {
        val state = _uiState.value
        var isValid = true
        if (!isPasswordValid(state.password)) {
            _uiState.update { it.copy(passwordError = "Password must be at least 8 characters, include letters, numbers, and special characters.") }
            isValid = false
        }
        if (state.flowType == "signup" && state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }
        if (!isValid) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _uiState.update { it.copy(isLoading = false) }
            if (state.flowType == "login") {
                if (state.email == "test@example.com" && state.password == "password") {
                    _snackbarMessage.emit("Login successful for ${state.email}")
                } else {
                    _snackbarMessage.emit("Authentication failed. Please try again.")
                    _uiState.update { it.copy(passwordError = "Invalid credentials") }
                }
            } else if (state.flowType == "signup") {
                _navigationEvent.emit(AppScreen.EmailSignUpScreen.createRoute(state.flowType))
            }
        }
    }

    companion object {
        fun provideFactory(
            authService: AuthService,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null
        ): ViewModelProvider.Factory = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                if (modelClass.isAssignableFrom(EmailAuthViewModel::class.java)) {
                    return EmailAuthViewModel(handle, authService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
} 