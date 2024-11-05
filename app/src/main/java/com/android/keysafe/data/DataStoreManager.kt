package com.android.keysafe.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val PASSWORD = stringPreferencesKey("PASSWORD")
    }

    suspend fun saveToDataStore(loginPassword: LoginPassword) {
        context.preferenceDataStore.edit {
            it[PASSWORD] = loginPassword.loginPassword
        }
    }

    fun getFromDataStore() = context.preferenceDataStore.data.map {
        LoginPassword(
            loginPassword = it[PASSWORD] ?: ""
        )
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }

}