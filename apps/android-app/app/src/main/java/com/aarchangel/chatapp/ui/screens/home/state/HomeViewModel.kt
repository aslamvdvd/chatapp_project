package com.aarchangel.chatapp.ui.screens.home.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val SELECTED_TAB_KEY = "selected_tab"
    }

    private val _selectedTabIndex = MutableStateFlow(savedStateHandle[SELECTED_TAB_KEY] ?: 0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
        savedStateHandle[SELECTED_TAB_KEY] = index
    }
} 