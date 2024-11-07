package com.android.keysafe.navController

import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen

@Serializable
data object PasswordListScreen

@Serializable
data class PasswordDetailScreen(val id: Int)

@Serializable
data object RegisterScreen