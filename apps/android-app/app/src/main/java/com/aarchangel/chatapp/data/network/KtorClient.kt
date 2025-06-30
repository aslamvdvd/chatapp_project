package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.BuildConfig
import com.aarchangel.chatapp.data.TokenStorage
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val instance: HttpClient by lazy {
        HttpClient(Android) {
            expectSuccess = false

            defaultRequest {
                url(BuildConfig.API_URL)
                contentType(ContentType.Application.Json)
                val token = TokenStorage.getToken()
                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
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
        }
    }
} 