package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aarchangel.chatapp.di.AppContainer
import com.aarchangel.chatapp.ui.screens.friends.FriendListViewModel
import com.aarchangel.chatapp.ui.screens.search.SearchViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FriendListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                FriendListViewModel(container.friendService) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SearchViewModel(container.friendService) as T
            }
            // Add other view models here as needed
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}