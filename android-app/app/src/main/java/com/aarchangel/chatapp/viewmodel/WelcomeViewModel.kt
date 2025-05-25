package com.aarchangel.chatapp.viewmodel

// ChatApp by aarchangel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the WelcomeScreen.
 * Handles navigation events triggered by user actions on the WelcomeScreen.
 * // ChatApp by aarchangel
 */
class WelcomeViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Called when the user clicks the "Sign Up" button.
     * Triggers navigation to the AuthOptions screen with flowType 'signup'.
     */
    fun onSignUpClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AppScreen.AuthOptions.createRoute("signup"))
        }
    }

    /**
     * Called when the user clicks the "Log In" button.
     * Triggers navigation to the AuthOptions screen with flowType 'login'.
     */
    fun onLoginClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(AppScreen.AuthOptions.createRoute("login"))
        }
    }
} 