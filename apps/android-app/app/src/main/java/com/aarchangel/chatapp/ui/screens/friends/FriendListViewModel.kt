package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.model.dto.FriendListItem
import com.aarchangel.chatapp.model.dto.PaginatedResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class FriendListUiState {
    object Loading : FriendListUiState()
    data class Success(
        val items: List<Any>, 
        val hasMore: Boolean, 
        val type: ListType
    ) : FriendListUiState()
    data class Error(val message: String) : FriendListUiState()

    enum class ListType {
        FRIENDS, INCOMING_REQUESTS, BLOCKED_USERS
    }
}

class FriendListViewModel(private val friendService: FriendService) : ViewModel() {
    var uiState: FriendListUiState by mutableStateOf(FriendListUiState.Loading)
        private set

    private val _snackbarMessages = Channel<String>()
    val snackbarMessages = _snackbarMessages.receiveAsFlow()

    private var currentPage = 1
    private val defaultLimit = 20
    private var currentListType = FriendListUiState.ListType.FRIENDS

    init {
        fetchFriends(
            isInitialLoad = true, 
            listType = FriendListUiState.ListType.FRIENDS,
            page = 1,
            limit = defaultLimit
        )
    }

    fun fetchFriends(
        isInitialLoad: Boolean = false, 
        listType: FriendListUiState.ListType = FriendListUiState.ListType.FRIENDS,
        page: Int = currentPage,
        limit: Int = defaultLimit
    ) {
        var currentPageValue = page

        if (uiState is FriendListUiState.Loading && !isInitialLoad) return

        // Reset page if list type changes
        if (currentListType != listType) {
            currentPage = 1
            currentPageValue = 1
        }
        currentListType = listType

        val currentItems = if (uiState is FriendListUiState.Success && currentListType == (uiState as FriendListUiState.Success).type) {
            (uiState as FriendListUiState.Success).items
        } else {
            emptyList()
        }

        uiState = FriendListUiState.Loading

        viewModelScope.launch {
            try {
                val response = when (listType) {
                    FriendListUiState.ListType.FRIENDS -> {
                        val friendsResponse = friendService.getFriends(page = currentPageValue, limit = limit)
                        PaginatedResponse(
                            items = friendsResponse.items,
                            has_more = friendsResponse.has_more
                        )
                    }
                    FriendListUiState.ListType.INCOMING_REQUESTS -> {
                        friendService.getIncomingFriendRequests(page = currentPageValue, limit = limit)
                    }
                    FriendListUiState.ListType.BLOCKED_USERS -> {
                        // TODO: Implement blocked users fetch
                        PaginatedResponse(items = emptyList(), has_more = false)
                    }
                }

                uiState = FriendListUiState.Success(
                    items = currentItems + response.items, 
                    hasMore = response.has_more,
                    type = listType
                )

                if (response.has_more) {
                    currentPage++
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                uiState = FriendListUiState.Error(errorMessage)
                _snackbarMessages.send(errorMessage)
            }
        }
    }

    fun acceptFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                friendService.acceptFriendRequest(requestId)
                // Refresh incoming requests
                fetchFriends(
                    listType = FriendListUiState.ListType.INCOMING_REQUESTS,
                    page = 1,
                    limit = defaultLimit
                )
            } catch (e: Exception) {
                _snackbarMessages.send(e.message ?: "Failed to accept friend request")
            }
        }
    }

    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                friendService.rejectFriendRequest(requestId)
                // Refresh incoming requests
                fetchFriends(
                    listType = FriendListUiState.ListType.INCOMING_REQUESTS,
                    page = 1,
                    limit = defaultLimit
                )
            } catch (e: Exception) {
                _snackbarMessages.send(e.message ?: "Failed to reject friend request")
            }
        }
    }
} 