package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.FriendService
import com.aarchangel.chatapp.model.dto.FriendListItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class FriendListUiState {
    object Loading : FriendListUiState()
    data class Success(val friends: List<FriendListItem>, val hasMore: Boolean) : FriendListUiState()
    data class Error(val message: String) : FriendListUiState()
}

class FriendListViewModel(private val friendService: FriendService) : ViewModel() {
    var uiState: FriendListUiState by mutableStateOf(FriendListUiState.Loading)
        private set

    private val _snackbarMessages = Channel<String>()
    val snackbarMessages = _snackbarMessages.receiveAsFlow()

    private var currentPage = 1
    private val limit = 20

    init {
        fetchFriends()
    }

    fun fetchFriends() {
        if (uiState is FriendListUiState.Loading) return
        uiState = FriendListUiState.Loading

        viewModelScope.launch {
            try {
                val response = friendService.getFriends(currentPage, limit)
                val currentFriends = if (uiState is FriendListUiState.Success) {
                    (uiState as FriendListUiState.Success).friends
                } else {
                    emptyList()
                }
                uiState = FriendListUiState.Success(currentFriends + response.items, response.has_more)
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
} 