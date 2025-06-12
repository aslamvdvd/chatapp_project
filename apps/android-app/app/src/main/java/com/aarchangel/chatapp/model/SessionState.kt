package com.aarchangel.chatapp.model

import com.aarchangel.chatapp.dto.UserProfileDto

sealed class SessionState {
    object Loading : SessionState()
    data class LoggedIn(val userProfile: UserProfileDto) : SessionState()
    object LoggedOut : SessionState()
} 