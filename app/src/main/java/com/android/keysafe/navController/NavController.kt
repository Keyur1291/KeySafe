package com.android.keysafe.navController

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.android.keysafe.components.BiometricPromptManager
import com.android.keysafe.PasswordViewModel
import com.android.keysafe.view.LoginScreen
import com.android.keysafe.view.PasswordDetailScreen
import com.android.keysafe.view.PasswordList
import com.android.keysafe.view.RegisterScreen
import com.android.keysafe.model.DataStoreManager

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavController(
    modifier: Modifier = Modifier,
    promptManager: BiometricPromptManager,
    viewModel: PasswordViewModel,
    preferenceDataStore: DataStore<Preferences>,
    dataStoreManager: DataStoreManager
) {

    SharedTransitionLayout {

        val navController = rememberNavController()
        val animatedSize = SharedTransitionScope.PlaceHolderSize.animatedSize

        NavHost(navController = navController, startDestination = RegisterScreen) {

            composable<RegisterScreen> {
                RegisterScreen(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    viewModel = viewModel,
                    preferenceDataStore = preferenceDataStore,
                    dataStoreManager = dataStoreManager
                )
            }

            composable<LoginScreen> {
                LoginScreen(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    promptManager = promptManager,
                    viewModel = viewModel,
                    dataStoreManager = dataStoreManager
                )
            }

            composable<PasswordListScreen> {
                PasswordList(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    viewModel = viewModel,
                    placeHolderSize = animatedSize,
                    dataStoreManager = dataStoreManager
                )
            }

            composable<PasswordDetailScreen> {

                val arguments = it.toRoute<PasswordDetailScreen>()

                PasswordDetailScreen(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    viewModel = viewModel,
                    placeHolderSize = animatedSize,
                    id = arguments.id
                )
            }
        }
    }

}