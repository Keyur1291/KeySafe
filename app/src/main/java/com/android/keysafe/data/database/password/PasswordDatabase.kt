package com.android.keysafe.data.database.password

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.keysafe.data.model.Password

@Database(
    entities = [Password::class],
    version = 1
)
abstract class PasswordDatabase: RoomDatabase() {

    abstract val passwordDao: PasswordDao

}