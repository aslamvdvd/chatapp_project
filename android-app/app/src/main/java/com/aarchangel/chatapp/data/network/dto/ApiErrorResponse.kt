package com.aarchangel.chatapp.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val message: String, // General error message
    val errors: List<ApiErrorDetail>? = null // Optional list of field-specific errors
) 