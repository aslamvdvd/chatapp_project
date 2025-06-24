package com.aarchangel.chatapp.ui.screens.home.env

/**
 * A sealed interface to provide environment-specific configurations.
 * This allows for easy mocking of values from `AppConfig` in composable previews.
 */
sealed interface AppEnv {
    val platformName: String
    val platformSlogan: String
    val groupChatAlias: String
    val channelAlias: String
}

/**
 * The production implementation that pulls values directly from the `AppConfig` object.
 */
object ProductionAppEnv : AppEnv {
    override val platformName: String = com.aarchangel.chatapp.config.AppConfig.PLATFORM_NAME
    override val platformSlogan: String = com.aarchangel.chatapp.config.AppConfig.PLATFORM_SLOGAN
    override val groupChatAlias: String = com.aarchangel.chatapp.config.AppConfig.GROUP_CHAT_NAME
    override val channelAlias: String = "Channels" // Assuming no alias in AppConfig yet
}

/**
 * A mock implementation for use in Jetpack Compose `@Preview` annotations.
 */
object MockAppEnv : AppEnv {
    override val platformName: String = "GhostTalk"
    override val platformSlogan: String = "Private. Modern. Secure."
    override val groupChatAlias: String = "Circles"
    override val channelAlias: String = "Channels"
} 