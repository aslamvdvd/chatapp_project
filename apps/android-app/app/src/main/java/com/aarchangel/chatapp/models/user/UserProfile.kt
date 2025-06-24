package com.aarchangel.chatapp.models.user

import java.util.Date

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val created_at: Date,
    val updated_at: Date
) 