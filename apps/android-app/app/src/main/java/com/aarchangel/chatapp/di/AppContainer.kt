package com.aarchangel.chatapp.di

import android.content.Context
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.local.PreferenceManager
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.data.network.KtorClient

interface AppContainer {
    val authService: AuthService
    val friendService: FriendService
    val authRepository: AuthRepository
    val preferenceManager: PreferenceManager
    val tokenStorage: TokenStorage
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val httpClient = KtorClient.instance

    override val authService: AuthService by lazy {
        AuthServiceImpl(httpClient)
    }

    override val friendService: FriendService by lazy {
        FriendService(httpClient)
    }

    override val tokenStorage: TokenStorage by lazy {
        TokenStorage(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(authService, tokenStorage)
    }

    override val preferenceManager: PreferenceManager by lazy {
        PreferenceManager(context)
    }
} 