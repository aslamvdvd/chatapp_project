package com.aarchangel.chatapp

import android.app.Application
import com.aarchangel.chatapp.di.AppContainer
import com.aarchangel.chatapp.di.DefaultAppContainer

class ChatApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
} 