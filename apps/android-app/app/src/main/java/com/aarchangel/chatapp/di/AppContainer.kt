package com.aarchangel.chatapp.di

import android.content.Context
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.local.PreferenceManager
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.data.network.KtorClient
import io.ktor.client.HttpClient

interface AppContainer {
    val authService: AuthService
    val friendService: FriendService
    val authRepository: AuthRepository
    val preferenceManager: PreferenceManager
    val tokenStorage: TokenStorage
    fun getHttpClient(): HttpClient
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val tokenStorage: TokenStorage by lazy {
        TokenStorage(context)
    }

    private var _httpClient: HttpClient? = null

    override fun getHttpClient(): HttpClient {
        if (_httpClient == null) {
            _httpClient = KtorClient.getInstance(tokenStorage)
        }
        return _httpClient!!
    }

    fun resetHttpClient() {
        _httpClient = null
    }

    override val authService: AuthService by lazy {
        AuthServiceImpl(getHttpClient())
    }

    override val friendService: FriendService by lazy {
        FriendService(getHttpClient())
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(authService, tokenStorage)
    }

    override val preferenceManager: PreferenceManager by lazy {
        PreferenceManager(context)
    }
} 