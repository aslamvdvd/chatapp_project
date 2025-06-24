package com.aarchangel.chatapp.ui.screens.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.network.ApiService
import com.aarchangel.chatapp.models.user.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Mock data class
data class Friend(
    val id: String,
    val fullName: String,
    val username: String,
    val profilePicUrl: String? = null
)

sealed interface FriendListUiState {
    object Loading : FriendListUiState
    data class Success(val friends: List<UserProfile>) : FriendListUiState
    object Empty : FriendListUiState
    data class Error(val message: String) : FriendListUiState
}

class FriendViewModel(private val tokenStorage: TokenStorage) : ViewModel() {
    private val _uiState = MutableStateFlow<FriendListUiState>(FriendListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _uiState.value = FriendListUiState.Loading
            try {
                val token = tokenStorage.getToken()
                if (token != null) {
                    val friends = ApiService.instance.getFriendsList("Bearer $token")
                    _uiState.value = if (friends.isEmpty()) {
                        FriendListUiState.Empty
                    } else {
                        FriendListUiState.Success(friends)
                    }
                } else {
                    _uiState.value = FriendListUiState.Error("Authentication token not found.")
                }
            } catch (e: Exception) {
                _uiState.value = FriendListUiState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
} 