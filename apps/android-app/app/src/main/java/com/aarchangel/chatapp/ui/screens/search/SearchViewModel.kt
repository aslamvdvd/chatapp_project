package com.aarchangel.chatapp.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.model.dto.AcceptRequestPayload
import com.aarchangel.chatapp.model.dto.CancelRequestPayload
import com.aarchangel.chatapp.model.dto.FriendRequestPayload
import com.aarchangel.chatapp.model.dto.FriendStatus
import com.aarchangel.chatapp.model.dto.RejectRequestPayload
import com.aarchangel.chatapp.model.dto.UserSearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val users: List<UserSearchResult>, val hasMore: Boolean) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(private val friendService: FriendService) : ViewModel() {
    var uiState: SearchUiState by mutableStateOf(SearchUiState.Idle)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null
    private var currentPage = 1
    private val limit = 20

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            if (query.isNotBlank()) {
                searchUsers(true)
            } else {
                uiState = SearchUiState.Idle
            }
        }
    }

    fun searchUsers(fromScratch: Boolean) {
        if (uiState is SearchUiState.Loading) return

        if (fromScratch) {
            currentPage = 1
        }
        uiState = SearchUiState.Loading

        viewModelScope.launch {
            try {
                val response = friendService.searchUsers(searchQuery, currentPage, limit)
                val currentResults = if (!fromScratch && uiState is SearchUiState.Success) {
                    (uiState as SearchUiState.Success).users
                } else {
                    emptyList()
                }
                uiState = SearchUiState.Success(currentResults + response.items, response.has_more)
                if (response.has_more) {
                    currentPage++
                }
            } catch (e: Exception) {
                uiState = SearchUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                friendService.sendFriendRequest(FriendRequestPayload(receiver_id = userId))
                updateFriendStatus(userId, FriendStatus.PENDING_OUTGOING)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                // The backend needs the request_id, not the user_id. This is a simplification.
                // In a real app, you would need to get the request_id from somewhere.
                friendService.acceptFriendRequest(AcceptRequestPayload(request_id = userId))
                updateFriendStatus(userId, FriendStatus.ACCEPTED)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun rejectFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                friendService.rejectFriendRequest(RejectRequestPayload(request_id = userId))
                updateFriendStatus(userId, FriendStatus.NONE)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun cancelFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                friendService.cancelFriendRequest(CancelRequestPayload(request_id = userId))
                updateFriendStatus(userId, FriendStatus.NONE)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun updateFriendStatus(userId: String, newStatus: FriendStatus) {
        if (uiState is SearchUiState.Success) {
            val currentUsers = (uiState as SearchUiState.Success).users.toMutableList()
            val userIndex = currentUsers.indexOfFirst { it.user.id == userId }
            if (userIndex != -1) {
                val updatedUser = currentUsers[userIndex].copy(friend_status = newStatus)
                currentUsers[userIndex] = updatedUser
                uiState = (uiState as SearchUiState.Success).copy(users = currentUsers)
            }
        }
    }
} 