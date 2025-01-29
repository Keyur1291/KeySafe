package com.android.keysafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.keysafe.data.database.password.PasswordDao
import com.android.keysafe.di.PasswordEvent
import com.android.keysafe.di.PasswordState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val passwordDao: PasswordDao,
) : ViewModel() {

    private val _passwordsList = passwordDao.getPasswordOrderedByTitle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _passwordState = MutableStateFlow(PasswordState())
    val passwordState = combine(_passwordState, _passwordsList) { state, passwordsList ->
        state.copy(
            passwordsList = passwordsList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PasswordState())

    fun onEvent(passwordEvent: PasswordEvent) {

        when (passwordEvent) {

            is PasswordEvent.DeletePassword -> {
                viewModelScope.launch {
                    passwordDao.deletePassword(password = passwordEvent.password)
                }
            }

            is PasswordEvent.SavePassword -> {
                viewModelScope.launch {
                    passwordDao.upsertPassword(password = passwordEvent.password)
                }
            }

            is PasswordEvent.SetNote -> {
                _passwordState.update {
                    it.copy(
                        note = passwordEvent.note
                    )
                }
            }

            is PasswordEvent.SetPassword -> {
                _passwordState.update {
                    it.copy(password = passwordEvent.passwordValue)
                }
            }

            is PasswordEvent.SetTitle -> {
                _passwordState.update {
                    it.copy(title = passwordEvent.title)
                }
            }

            is PasswordEvent.SetUserName -> {
                _passwordState.update {
                    it.copy(userName = passwordEvent.userName)
                }
            }
        }
    }
}