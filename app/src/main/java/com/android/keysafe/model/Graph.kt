package com.android.keysafe.model

import android.content.Context
import androidx.room.Room

object Graph {

    private lateinit var database: PasswordDatabase


    val passwordRepository by lazy {
        PasswordRepository(database.passwordDao())
    }

    fun provide(context: Context) {

        database = Room.databaseBuilder(
            context,
            PasswordDatabase::class.java,
            "passwords.db")
            .build()
    }

}