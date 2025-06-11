package com.aarchangel.chatapp.data

import com.aarchangel.chatapp.network.AuthService

class AuthRepository(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) {
    suspend fun clearJwt() {
        tokenStorage.clearToken()
    }

    suspend fun getToken(): String? {
        return tokenStorage.getToken()
    }
} 