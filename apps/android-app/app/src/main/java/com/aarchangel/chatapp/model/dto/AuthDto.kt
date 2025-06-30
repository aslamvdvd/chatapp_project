package com.aarchangel.chatapp.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email_or_username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String,
    val email: String,
    val profilePic: String? = null
) 