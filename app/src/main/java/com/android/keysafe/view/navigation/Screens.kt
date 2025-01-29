package com.android.keysafe.navController

import kotlinx.serialization.Serializable

sealed class SubGraph {

    @Serializable
    data object AuthGraph: SubGraph()

    @Serializable
    data object HomeGraph: SubGraph()
}

sealed class Destinations {

    @Serializable
    data object ListPane: Destinations()

    @Serializable
    data object LoginScreen: Destinations()

    @Serializable
    data object PasswordListScreen: Destinations()

    @Serializable
    data class PasswordDetailScreen(val id: Int): Destinations()

    @Serializable
    data object RegisterScreen: Destinations()

    @Serializable
    data object SettingsScreen: Destinations()
}