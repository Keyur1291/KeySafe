package com.android.keysafe.model

import kotlinx.coroutines.flow.Flow

class PasswordRepository(private val passwordDao: PasswordDao) {

    suspend fun upsertPassword(password: Password) {
        passwordDao.upsertPassword(password)
    }

    suspend fun deletePassword(password: Password) {
        passwordDao.deletePassword(password)
    }

    fun getPasswordsOrderedByTitle(): Flow<List<Password>> = passwordDao.getPasswordOrderedByTitle()
}