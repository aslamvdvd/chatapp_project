package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the AuthOptionsScreen.
 * Handles UI logic, state, and navigation events for the authentication options.
 * // ChatApp by aarchangel
 */
class AuthOptionsViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    /**
     * Called when the "Continue with Email" button is clicked.
     * Navigates to EmailEntryScreen for signup, which now includes email and password fields
     * or LoginScreen for login.
     */
    fun onContinueWithEmailClicked(flowType: String) {
        viewModelScope.launch {
            if (flowType == "signup") {
                _navigationEvent.emit(AppScreen.EmailSignUpScreen.createRoute(flowType))
            } else { // "login"
                _navigationEvent.emit(AppScreen.Login.route) // Login screen for login flow
            }
        }
    }

    /**
     * Called when the "Continue with Google" button is clicked.
     * Shows a Snackbar message indicating the feature is coming soon.
     */
    fun onContinueWithGoogleClicked() {
        viewModelScope.launch {
            _snackbarMessage.emit("Continue with Google is coming soon!")
        }
    }

    /**
     * Called when the "Sign in with Apple" button is clicked.
     * Shows a Snackbar message indicating the feature is coming soon.
     */
    fun onSignInWithAppleClicked() { // Renamed from onContinueWithAppleClicked to match button text
        viewModelScope.launch {
            _snackbarMessage.emit("Sign in with Apple is coming soon!")
        }
    }

    /**
     * Called when the "Use Phone Number" button is clicked.
     * Shows a Snackbar message indicating the feature is coming soon.
     */
    fun onUsePhoneNumberClicked() { // Renamed from onContinueWithPhoneClicked to match button text
        viewModelScope.launch {
            _snackbarMessage.emit("Use Phone Number is coming soon!")
        }
    }

    /**
     * Called when the "Back" button is clicked.
     * Triggers navigation back to the Welcome screen.
     * Note: The NavController's popBackStack will handle the actual back navigation.
     * This event is to inform the NavController to pop to a specific destination if needed,
     * or could be used for more complex back navigation logic if required in the future.
     * For simple pop, this might not emit if MainActivity directly handles navcontroller.popBackStack().
     * However, to keep logic in VM, we can emit a special "navigate_back" event if needed.
     * For now, we'll assume NavController's popBackStack is sufficient and called from UI lambda.
     */
    fun onBackClicked() {
        // In a more complex scenario, we might emit a specific event for the NavController
        // to pop to AppScreen.Welcome.route, but for simple pop, NavController.popBackStack() is often enough.
        // Emitting an event to signify the intention for clarity or if specific pop logic is needed.
        viewModelScope.launch {
            // _navigationEvent.emit(AppScreen.Welcome.route) // Or a special "POP_TO_WELCOME" event
            // For now, let's assume simple popBackStack is handled by NavController in MainActivity/Screen
            // based on user clicking a UI back button.
            // If the back button *itself* is fully managed by VM, then emit would be needed.
        }
    }
} 