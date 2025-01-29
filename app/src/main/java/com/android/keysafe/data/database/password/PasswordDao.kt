package com.android.keysafe.data.database.password

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.keysafe.data.model.Password
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PasswordDao {

    @Upsert
    abstract suspend fun upsertPassword(password: Password)

    @Delete
    abstract suspend fun deletePassword(password: Password)

    @Query("SELECT * FROM password ORDER BY title ASC")
    abstract fun getPasswordOrderedByTitle(): Flow<List<Password>>

}