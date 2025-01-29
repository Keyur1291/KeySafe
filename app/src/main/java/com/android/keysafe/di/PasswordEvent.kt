package com.android.keysafe.di

import com.android.keysafe.data.model.Password

sealed interface PasswordEvent {

    data class SavePassword(val password: Password): PasswordEvent
    data class DeletePassword(val password: Password): PasswordEvent
    data class SetTitle(val title: String): PasswordEvent
    data class SetUserName(val userName: String): PasswordEvent
    data class SetPassword(val passwordValue: String): PasswordEvent
    data class SetNote(val note: String): PasswordEvent
}