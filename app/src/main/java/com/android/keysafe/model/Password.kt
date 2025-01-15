package com.android.keysafe.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Password(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var userName: String,
    var password: String,
    var note: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombination = listOf(
            "$title $userName" ,
            "${title.first()}",
            "${userName.first()} ${userName.first()}"
        )
        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }
}