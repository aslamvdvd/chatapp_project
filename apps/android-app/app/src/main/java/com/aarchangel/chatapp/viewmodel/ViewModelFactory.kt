package com.aarchangel.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aarchangel.chatapp.di.AppContainer
import com.aarchangel.chatapp.di.DefaultAppContainer
import com.aarchangel.chatapp.ui.screens.friends.FriendListViewModel
import com.aarchangel.chatapp.ui.screens.search.SearchViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                MainViewModel(container.authService, container.authRepository, container.preferenceManager) as T
            }
            modelClass.isAssignableFrom(EmailAuthViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmailAuthViewModel(container.authService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(
                    container.authService,
                    container.tokenStorage,
                    container as DefaultAppContainer
                ) as T
            }
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