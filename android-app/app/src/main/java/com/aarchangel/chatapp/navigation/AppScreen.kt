package com.aarchangel.chatapp.navigation

// ChatApp by aarchangel

/**
 * Defines the routes for different screens in the application in a type-safe manner.
 * Each object represents a distinct screen.
 * // ChatApp by aarchangel
 */
sealed class AppScreen(val route: String) {
    /**
     * Represents the Splash screen, the initial entry point of the app.
     */
    object Splash : AppScreen("splash")

    /**
     * Represents the Welcome screen.
     */
    object Welcome : AppScreen("welcome")

    /**
     * Represents the Authentication Entry screen where users choose to log in or sign up.
     */
    object AuthEntry : AppScreen("auth_entry")

    /**
     * Represents the screen where users choose their sign-up method (e.g., email, phone).
     */
    object SignUpMethod : AppScreen("signup_method")

    /**
     * Represents the screen where users choose their login method.
     */
    object LoginMethod : AppScreen("login_method")

    /**
     * Represents the form for signing up with an email and password.
     */
    object EmailSignUp : AppScreen("email_signup")

    /**
     * Represents the form for logging in with an email and password.
     */
    object EmailLogin : AppScreen("email_login")

    /**
     * Represents the Home screen, the main screen after authentication.
     */
    object Home : AppScreen("home")
}