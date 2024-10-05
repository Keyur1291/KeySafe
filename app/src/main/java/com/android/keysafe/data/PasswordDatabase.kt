package com.android.keysafe.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Password::class],
    version = 1
)
abstract class PasswordDatabase: RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

}