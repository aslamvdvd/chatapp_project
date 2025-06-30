package com.aarchangel.chatapp.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class FriendListItem(
    val id: String,
    val user: UserDto,
    val status: String // "accepted", etc.
)

@Serializable
data class FriendRequestPayload(
    val receiver_id: String
)

@Serializable
data class AcceptRequestPayload(
    val request_id: String
)

@Serializable
data class RejectRequestPayload(
    val request_id: String
)

@Serializable
data class CancelRequestPayload(
    val request_id: String
)

@Serializable
data class UserSearchResult(
    val userId: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val requestId: String? = null,
    val status: FriendStatus
)

@Serializable
enum class FriendStatus {
    NONE,
    PENDING_INCOMING,
    PENDING_OUTGOING,
    ACCEPTED
}

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    val limit: Int,
    val has_more: Boolean,
) 