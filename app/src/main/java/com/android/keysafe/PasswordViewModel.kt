package com.android.keysafe

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.keysafe.data.Password
import com.android.keysafe.data.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
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