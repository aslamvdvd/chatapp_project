package com.aarchangel.chatapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val HAS_AGREED_TO_TERMS = booleanPreferencesKey("has_agreed_to_terms")
    }

    val hasAgreedToTerms: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAS_AGREED_TO_TERMS] ?: false
        }

    suspend fun setTermsAgreement(agreed: Boolean) {
        context.dataStore.edit { settings ->
            settings[HAS_AGREED_TO_TERMS] = agreed
        }
    }
} 