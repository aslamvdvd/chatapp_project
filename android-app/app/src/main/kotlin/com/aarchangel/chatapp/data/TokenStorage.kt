package com.aarchangel.chatapp.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStorage(context: Context) {

    // Note: MasterKeys.getOrCreate is deprecated. 
    // For production apps, consider using MasterKey.Builder for more control or a different approach
    // if specific key management (like hardware-backed keystore) is required and MasterKeys is too simple.
    // For this task, we'll proceed with the deprecated version as per the prompt's example.
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "secure_ghosttalk_prefs", // Changed filename slightly for clarity
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val JWT_TOKEN_KEY = "jwt_token"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(JWT_TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(JWT_TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(JWT_TOKEN_KEY).apply()
    }
} 