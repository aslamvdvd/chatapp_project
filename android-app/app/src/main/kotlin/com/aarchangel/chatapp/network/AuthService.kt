package com.aarchangel.chatapp.network

import com.aarchangel.chatapp.BuildConfig
import com.aarchangel.chatapp.dto.LoginRequest
import com.aarchangel.chatapp.dto.LoginResponse
import com.aarchangel.chatapp.dto.UserProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    sealed class Error(val message: String? = null) : NetworkResult<Nothing>() {
        class Unauthorized(message: String? = "Invalid credentials") : Error(message)
        class BadRequest(message: String? = "Invalid input") : Error(message)
        class ServerError(message: String? = "Server error occurred") : Error(message)
        class NetworkError(message: String? = "Network error occurred") : Error(message)
        class UnknownError(message: String? = "An unknown error occurred") : Error(message)
    }
}

interface AuthService {
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
    suspend fun getProfile(token: String): NetworkResult<UserProfileDto>
}

class AuthServiceImpl : AuthService {

    private val client = HttpClient(Android) {
        expectSuccess = false // Handle HTTP errors manually
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for robust parsing
            })
        }
        install(Logging) {
            level = LogLevel.ALL // Log requests and responses for debugging
        }
    }

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return try {
            val response: HttpResponse = client.post("${BuildConfig.API_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            when (response.status.value) {
                200 -> NetworkResult.Success(response.body())
                400 -> NetworkResult.Error.BadRequest(response.body<String?>() ?: "Bad Request")
                401 -> NetworkResult.Error.Unauthorized(response.body<String?>() ?: "Unauthorized")
                500 -> NetworkResult.Error.ServerError(response.body<String?>() ?: "Internal Server Error")
                else -> NetworkResult.Error.UnknownError("Received status: ${response.status.value} - ${response.body<String?>()}")
            }
        } catch (e: io.ktor.client.plugins.ClientRequestException) { // Covers 4xx generally if not caught by status code
            when (e.response.status.value) {
                400 -> NetworkResult.Error.BadRequest(e.response.body<String?>() ?: e.message)
                401 -> NetworkResult.Error.Unauthorized(e.response.body<String?>() ?: e.message)
                else -> NetworkResult.Error.NetworkError("Client error: ${e.message}")
            }
        } catch (e: io.ktor.client.plugins.ServerResponseException) { // Covers 5xx generally
             NetworkResult.Error.ServerError("Server error: ${e.message}")
        } catch (e: io.ktor.utils.io.errors.IOException) {
            NetworkResult.Error.NetworkError("Network connection error: ${e.message}")
        } catch (e: Exception) {
            // Log.e("AuthServiceImpl", "Login failed: ${e.localizedMessage}", e) // Consider logging the exception
            NetworkResult.Error.UnknownError("An unexpected error occurred: ${e.message}")
        }
    }

    override suspend fun getProfile(token: String): NetworkResult<UserProfileDto> {
        return try {
            val response: HttpResponse = client.get("${BuildConfig.API_URL}/auth/me") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            when (response.status.value) {
                200 -> NetworkResult.Success(response.body())
                401 -> NetworkResult.Error.Unauthorized("Unauthorized: Token is invalid or expired")
                else -> NetworkResult.Error.UnknownError("Received status: ${response.status.value} - ${response.body<String?>()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error.UnknownError("An unexpected error occurred: ${e.message}")
        }
    }
} 