package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.BuildConfig
import com.aarchangel.chatapp.data.network.dto.ApiErrorResponse
import com.aarchangel.chatapp.data.network.dto.SignUpRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface AuthService {
    suspend fun signUp(request: SignUpRequest): Result<Unit> // Using Result for simplicity here, can be a custom sealed class
}

class AuthServiceImpl : AuthService {

    private val client = HttpClient(Android) {
        expectSuccess = false // Handle HTTP errors manually

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for evolving APIs
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL // Log HTTP requests and responses
        }

        // Default request parameters if needed, e.g., base URL parts, headers
        // defaultRequest {
        //     contentType(ContentType.Application.Json)
        // }
    }

    override suspend fun signUp(request: SignUpRequest): Result<Unit> {
        return try {
            val response: HttpResponse = client.post("${BuildConfig.API_URL}/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.Created -> Result.success(Unit)
                HttpStatusCode.BadRequest -> {
                    val errorResponse = response.body<ApiErrorResponse>()
                    Result.failure(ValidationException(errorResponse))
                }
                HttpStatusCode.Conflict -> {
                    val errorResponse = response.body<ApiErrorResponse>()
                    Result.failure(ConflictException(errorResponse.message))
                }
                else -> {
                    val errorBody = response.bodyAsText()
                    Result.failure(Exception("Signup failed: ${response.status.value} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            // Handle network errors or unexpected issues
            Result.failure(e)
        }
    }
}

// Custom exceptions to carry typed error information
class ValidationException(val errorResponse: ApiErrorResponse) : Exception("Validation failed: ${errorResponse.message}")
class ConflictException(message: String) : Exception(message) 