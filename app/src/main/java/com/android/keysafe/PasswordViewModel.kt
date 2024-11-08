package com.android.keysafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.keysafe.model.Graph
import com.android.keysafe.model.Password
import com.android.keysafe.model.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class PasswordViewModel(
    private val passwordRepository: PasswordRepository = Graph.passwordRepository,
) : ViewModel() {

    var passwordTitleState by mutableStateOf("")
    var passwordUserNameState by mutableStateOf("")
    var passwordPasswordState by mutableStateOf("")
    var passwordNoteState by mutableStateOf("")
    lateinit var getPasswords: Flow<List<Password>>
    var textFieldEnabled by mutableStateOf(false)

    var authPasswordState by mutableStateOf("")
    var savePasswordState by mutableStateOf("")
    var confSavePasswordState by mutableStateOf("")

    var cardExpanded by mutableStateOf(false)
    var includeLower by mutableStateOf(true)
    var includeUpper by mutableStateOf(false)
    var includeDigit by mutableStateOf(false)
    var includeSpecialChars by mutableStateOf(false)
    var length by mutableIntStateOf(10)

    init {
        viewModelScope.launch {
            getPasswords = passwordRepository.getPasswordsOrderedByTitle()
        }

    }

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

    fun onSavePasswordChange(newSavePass: String) {
        savePasswordState = newSavePass
    }

    fun onConfSavePasswordChange(newConfSavePass: String) {
        confSavePasswordState = newConfSavePass
    }

    fun onAuthPasswordChange(newAuthPass: String) {
        authPasswordState = newAuthPass
    }

    fun onTitleChange(newTitle: String) {
        passwordTitleState = newTitle
    }

    fun onUserNameChange(newUserName: String) {
        passwordUserNameState = newUserName
    }

    fun onPasswordChange(newPassword: String) {
        passwordPasswordState = newPassword
    }

    fun onNoteChange(newNote: String) {
        passwordNoteState = newNote
    }

    fun upsertPassword(password: Password) {
        viewModelScope.launch(Dispatchers.IO) {
            passwordRepository.upsertPassword(password)
        }
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch(Dispatchers.IO) {
            passwordRepository.deletePassword(password)
        }
    }
}