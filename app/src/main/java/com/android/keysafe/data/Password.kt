package com.android.keysafe.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher
import org.bouncycastle.jce.provider.BouncyCastleProvider

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