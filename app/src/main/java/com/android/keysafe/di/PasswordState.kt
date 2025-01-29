package com.android.keysafe.di

import com.android.keysafe.data.model.Password
import kotlin.random.Random

data class PasswordState(
    var passwordsList: List<Password> = emptyList(),
    val id: Int = 0,
    var title: String = "",
    var userName: String = "",
    var password: String = "",
    var note: String = ""
) {
    fun generatePassword(length: Int, includeLower: Boolean, includeUpper: Boolean, includeDigits: Boolean, includeSpecialChars: Boolean): String {
        val chars = buildList {
            if (includeLower) addAll('a'..'z')
            if (includeUpper) addAll('A'..'Z')
            if (includeDigits) addAll('0'..'9')
            if (includeSpecialChars) addAll("!@#\$%^&*()_-+={}[]|;:<>,.?/~".toList())
        }

        return (1..length)
            .map { Random.nextInt(chars.size) }
            .map(chars::get)
            .joinToString("")
    }
}