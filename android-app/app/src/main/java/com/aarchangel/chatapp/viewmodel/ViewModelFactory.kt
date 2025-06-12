package com.aarchangel.chatapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val authService: AuthService by lazy { AuthServiceImpl() }
    private val tokenStorage: TokenStorage by lazy { TokenStorage(context.applicationContext) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EmailAuthViewModel::class.java) -> {
                EmailAuthViewModel(authService) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authService, tokenStorage) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 