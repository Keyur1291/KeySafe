package com.android.keysafe.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val BIOMETRIC = booleanPreferencesKey("BIOMETRIC")
    }

    suspend fun saveToDataStore(loginPassword: LoginPassword) {
        context.preferenceDataStore.edit {
            it[PASSWORD] = loginPassword.loginPassword
            it[BIOMETRIC] = loginPassword.biometricEnable
        }
    }

    fun getFromDataStore() = context.preferenceDataStore.data.map {
        LoginPassword(
            loginPassword = it[PASSWORD] ?: "",
            biometricEnable = it[BIOMETRIC] ?: true
        )
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }

}