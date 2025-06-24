package com.aarchangel.chatapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.ui.screens.home.viewmodels.FriendViewModel
import com.aarchangel.chatapp.ui.screens.search.SearchViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val authService: AuthService by lazy { AuthServiceImpl() }
    private val tokenStorage: TokenStorage by lazy { TokenStorage(context.applicationContext) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EmailAuthViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                EmailAuthViewModel(authService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(authService, tokenStorage) as T
            }
            modelClass.isAssignableFrom(FriendViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                FriendViewModel(tokenStorage) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SearchViewModel(tokenStorage) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 