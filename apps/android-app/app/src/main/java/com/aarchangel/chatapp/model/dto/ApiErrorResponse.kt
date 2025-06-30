package com.aarchangel.chatapp.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val message: String,
    val errors: Map<String, List<String>>? = null
) 