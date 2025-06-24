package com.aarchangel.chatapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarchangel.chatapp.data.network.ApiService
import com.aarchangel.chatapp.ui.model.UiUserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.aarchangel.chatapp.data.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(private val tokenStorage: TokenStorage) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<UiUserProfile>>(emptyList())
    val searchResults: StateFlow<List<UiUserProfile>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchUsers(query: String) {
        if (query.length < 3) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = ApiService.instance.searchUsers(query)
                // TODO: Add logic to determine FriendRequestStatus for each user
                _searchResults.value = results.map { UiUserProfile(it) }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendFriendRequest(receiverId: String) {
        viewModelScope.launch {
            try {
                val token = tokenStorage.getToken()
                if (token != null) {
                    ApiService.instance.sendFriendRequest("Bearer $token", receiverId)
                    // TODO: Update the UI to reflect the pending request
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val token = tokenStorage.getToken()
                if (token != null) {
                    ApiService.instance.acceptFriendRequest("Bearer $token", requestId)
                    // TODO: Update the UI to reflect the accepted request
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 