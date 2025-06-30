package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.model.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String? = null, val fieldErrors: Map<String, List<String>>? = null) : NetworkResult<Nothing>()
}

interface AuthService {
    suspend fun signUp(request: SignUpRequest): Result<Unit>
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
    suspend fun getProfile(): NetworkResult<UserProfileDto>
}

class AuthServiceImpl(private val client: HttpClient) : AuthService {

    override suspend fun signUp(request: SignUpRequest): Result<Unit> {
        return try {
            val response: HttpResponse = client.post("auth/signup") {
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

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return try {
            val response: HttpResponse = client.post("auth/login") {
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> NetworkResult.Success(response.body())
                HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                    val errorBody = response.body<ApiErrorResponse>()
                    NetworkResult.Error(errorBody.message, errorBody.errors)
                }
                HttpStatusCode.InternalServerError -> NetworkResult.Error("Server error. Please try again later.")
                else -> NetworkResult.Error("An unknown error occurred.")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getProfile(): NetworkResult<UserProfileDto> {
        return try {
            val response: HttpResponse = client.get("auth/me")

            when (response.status) {
                HttpStatusCode.OK -> NetworkResult.Success(response.body())
                HttpStatusCode.Unauthorized -> NetworkResult.Error("Unauthorized. Your session may have expired.")
                else -> NetworkResult.Error("An unknown error occurred.")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }
}

// Custom exceptions to carry typed error information
class ValidationException(val errorResponse: ApiErrorResponse) : Exception("Validation failed: ${errorResponse.message}")
class ConflictException(message: String) : Exception(message) 