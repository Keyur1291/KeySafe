package com.android.keysafe.data.database.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.keysafe.data.model.Auth
import kotlinx.coroutines.flow.map


const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val BIOMETRIC = booleanPreferencesKey("BIOMETRIC")
    }

    suspend fun saveToDataStore(auth: Auth) {
        context.preferenceDataStore.edit {
            it[PASSWORD] = auth.loginPassword
            it[BIOMETRIC] = auth.biometricEnable
        }
    }

    fun getFromDataStore() = context.preferenceDataStore.data.map {
        Auth(
            loginPassword = it[PASSWORD] ?: "",
            biometricEnable = it[BIOMETRIC] ?: true
        )
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }

}