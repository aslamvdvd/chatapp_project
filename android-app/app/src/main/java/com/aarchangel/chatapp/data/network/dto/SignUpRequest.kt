package com.aarchangel.chatapp.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val username: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("middle_name")
    val middleName: String?, // Optional
    @SerialName("last_name")
    val lastName: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String, // "YYYY-MM-DD"
    val gender: String?, // Optional
    val password: String,
    @SerialName("confirm_password")
    val confirmPassword: String
) 