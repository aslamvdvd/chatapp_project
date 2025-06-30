package com.aarchangel.chatapp.di

import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.data.network.KtorClient

interface AppContainer {
    val authService: AuthService
    val friendService: FriendService
}

class DefaultAppContainer : AppContainer {
    private val httpClient = KtorClient.instance

    override val authService: AuthService by lazy {
        AuthServiceImpl(httpClient)
    }

    override val friendService: FriendService by lazy {
        FriendService(httpClient)
    }
} 