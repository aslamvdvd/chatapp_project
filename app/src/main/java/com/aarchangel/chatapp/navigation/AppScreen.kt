package com.aarchangel.chatapp.navigation

// ChatApp by aarchangel

/**
 * Defines the routes for different screens in the application in a type-safe manner.
 * Each object represents a distinct screen or a screen with arguments.
 * // ChatApp by aarchangel
 */
sealed class AppScreen(val route: String) {
    /**
     * Represents the Welcome screen.
     */
    object Welcome : AppScreen("welcome")

    /**
     * Represents the Authentication Options screen.
     * It takes a `flowType` argument (e.g., "login" or "signup").
     */
    object AuthOptions : AppScreen("auth_options/{flowType}") {
        /**
         * Creates the route for AuthOptions with a specific flow type.
         * @param flowType The type of authentication flow (e.g., "login", "signup").
         * @return The complete route string for navigation.
         */
        fun createRoute(flowType: String) = "auth_options/$flowType"
    }

    /**
     * Represents the Email Authentication screen (currently a placeholder).
     */
    object EmailAuth : AppScreen("email_auth")

    /**
     * Represents the Email Entry screen for authentication.
     * Takes a `flowType` argument ("login" or "signup").
     */
    object EmailEntry : AppScreen("email_entry/{flowType}") {
        fun createRoute(flowType: String) = "email_entry/$flowType"
    }

    /**
     * Represents the Password Entry screen for authentication.
     * Takes `email` and `flowType` arguments.
     */
    object PasswordEntry : AppScreen("password_entry/{flowType}/{email}") {
        fun createRoute(flowType: String, email: String) = "password_entry/$flowType/$email"
    }

    /**
     * Represents the Login screen where user enters credentials.
     */
    object Login : AppScreen("login")

    /**
     * Represents the screen for entering additional user details during signup.
     * Takes `email` and `flowType` arguments.
     */
    object CreateAccountDetails : AppScreen("create_account_details/{flowType}/{email}") {
        fun createRoute(flowType: String, email: String) = "create_account_details/$flowType/$email"
    }

    // Add other screens here as the app grows
    // Example: object HomeScreen : AppScreen("home")
    // Example: object ChatScreen : AppScreen("chat/{chatId}") {
    // fun createRoute(chatId: String) = "chat/$chatId"
    // }
} 