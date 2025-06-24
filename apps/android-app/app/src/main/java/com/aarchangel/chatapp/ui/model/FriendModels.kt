package com.aarchangel.chatapp.ui.model

import com.aarchangel.chatapp.models.user.UserProfile

data class UiUserProfile(
    val profile: UserProfile,
    val requestStatus: FriendRequestStatus = FriendRequestStatus.NONE
)

enum class FriendRequestStatus {
    NONE,       // No request sent or received
    PENDING,    // Request sent by current user
    INCOMING,   // Request received by current user
    FRIENDS     // Already friends
}