package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.model.dto.AcceptRequestPayload
import com.aarchangel.chatapp.model.dto.CancelRequestPayload
import com.aarchangel.chatapp.model.dto.FriendListItem
import com.aarchangel.chatapp.model.dto.FriendRequestPayload
import com.aarchangel.chatapp.model.dto.PaginatedResponse
import com.aarchangel.chatapp.model.dto.RejectRequestPayload
import com.aarchangel.chatapp.model.dto.UserSearchResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FriendService(private val httpClient: HttpClient) {
    suspend fun getFriends(page: Int, limit: Int): PaginatedResponse<FriendListItem> {
        return httpClient.get("friends/list") {
            url {
                parameters.append("page", page.toString())
                parameters.append("limit", limit.toString())
            }
        }.body()
    }

    suspend fun searchUsers(query: String, page: Int, limit: Int): PaginatedResponse<UserSearchResult> {
        return httpClient.get("users/search") {
            url {
                parameters.append("query", query)
                parameters.append("page", page.toString())
                parameters.append("limit", limit.toString())
            }
        }.body()
    }

    suspend fun sendFriendRequest(userId: String) {
        httpClient.post("friends/request") {
            contentType(ContentType.Application.Json)
            setBody(FriendRequestPayload(receiver_id = userId))
        }.body<Unit>()
    }

    suspend fun acceptFriendRequest(requestId: String) {
        httpClient.post("friends/accept") {
            contentType(ContentType.Application.Json)
            setBody(AcceptRequestPayload(request_id = requestId))
        }.body<Unit>()
    }

    suspend fun rejectFriendRequest(requestId: String) {
        httpClient.post("friends/reject") {
            contentType(ContentType.Application.Json)
            setBody(RejectRequestPayload(request_id = requestId))
        }.body<Unit>()
    }

    suspend fun cancelFriendRequest(requestId: String) {
        httpClient.post("friends/cancel") {
            contentType(ContentType.Application.Json)
            setBody(CancelRequestPayload(request_id = requestId))
        }.body<Unit>()
    }
}

