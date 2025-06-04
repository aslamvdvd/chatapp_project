package com.aarchangel.chatapp.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDetail(
    val field: String? = null, // Field might not always be present (e.g., general error)
    val message: String
) 