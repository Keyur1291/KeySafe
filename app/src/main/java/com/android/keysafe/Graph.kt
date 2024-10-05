package com.android.keysafe

import android.content.Context
import androidx.room.Room
import com.android.keysafe.data.PasswordDatabase
import com.android.keysafe.data.PasswordRepository

object Graph {

    private lateinit var database: PasswordDatabase

    val passwordRepository by lazy {
        PasswordRepository(database.passwordDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, PasswordDatabase::class.java, "passwords.db").build()
    }

}