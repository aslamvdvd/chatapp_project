package com.aarchangel.chatapp.config

// ChatApp by aarchangel

/**
 * Singleton object for storing global application configurations and constants.
 * // ChatApp by aarchangel
 */
object AppConfig {
    /** The user-facing display name of the platform. */
    const val PLATFORM_NAME = "GhostTalk"
    /** The user-facing slogan for the platform. */
    const val PLATFORM_SLOGAN = "Private. Modern. Secure."
    /** Name for one-on-one chat features. */
    const val ONE_ON_ONE_CHAT_NAME = "Private Chat"
    /** Name for group chat features. */
    const val GROUP_CHAT_NAME = "Circle Chat"
    /** Default theme preference (e.g., "dark", "light", "system"). */
    const val DEFAULT_THEME = "dark"
    /** Flag to enable or disable guest mode. Currently not supported. */
    const val ENABLE_GUEST_MODE = false
} 