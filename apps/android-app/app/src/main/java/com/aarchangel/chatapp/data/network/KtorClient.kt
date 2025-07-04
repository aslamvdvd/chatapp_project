package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.BuildConfig
import com.aarchangel.chatapp.data.TokenStorage
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    fun getInstance(tokenStorage: TokenStorage): HttpClient {
        return HttpClient(Android) {
            expectSuccess = false

            defaultRequest {
                url(BuildConfig.API_URL)
                contentType(ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from your token storage
                        val token = tokenStorage.getToken()
                        if (token != null) {
                            BearerTokens(token, token) // Access and refresh tokens are the same for JWT
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        // This block is for refreshing tokens, which is beyond the scope of this fix
                        // For now, we'll just return null or throw an exception if refresh fails
                        null
                    }
                }
            }
        }
    }
} 