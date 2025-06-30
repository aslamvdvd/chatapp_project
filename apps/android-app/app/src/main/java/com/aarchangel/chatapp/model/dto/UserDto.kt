package com.aarchangel.chatapp.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val profile_pic: String? = null
) 