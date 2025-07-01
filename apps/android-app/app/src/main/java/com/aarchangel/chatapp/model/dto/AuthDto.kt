package com.aarchangel.chatapp.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email_or_username")
    val emailOrUsername: String,
    val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String
)

@Serializable
data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String // "YYYY-MM-DD"
)

@Serializable
data class SignUpResponse(
    val id: String,
    val email: String,
    val username: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String,
    val role: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String,
    val email: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("middle_name")
    val middleName: String?,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("created_at")
    val createdAt: String
) 