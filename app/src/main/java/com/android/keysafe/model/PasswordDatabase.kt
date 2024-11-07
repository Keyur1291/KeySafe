package com.android.keysafe.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Password::class],
    version = 1
)
abstract class PasswordDatabase: RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

}