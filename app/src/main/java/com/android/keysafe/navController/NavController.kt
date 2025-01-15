package com.android.keysafe.navController

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.android.keysafe.view.components.BiometricPromptManager
import com.android.keysafe.PasswordViewModel
import com.android.keysafe.view.LoginScreen
import com.android.keysafe.view.PasswordDetailScreen
import com.android.keysafe.view.PasswordList
import com.android.keysafe.view.RegisterScreen
import com.android.keysafe.model.DataStoreManager
import com.android.keysafe.navController.Destinations.SettingsScreen
import com.android.keysafe.navController.Destinations.PasswordListScreen
import com.android.keysafe.navController.Destinations.PasswordDetailScreen
import com.android.keysafe.navController.Destinations.LoginScreen
import com.android.keysafe.navController.Destinations.RegisterScreen
import com.android.keysafe.view.SettingsScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavController(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    promptManager: BiometricPromptManager,
    viewModel: PasswordViewModel,
    preferenceDataStore: DataStore<Preferences>,
    dataStoreManager: DataStoreManager,
    navController: NavHostController
) {

    SharedTransitionLayout {

        val animatedSize = SharedTransitionScope.PlaceHolderSize.animatedSize

        NavHost(navController = navController, startDestination = SubGraph.AuthGraph) {

            navigation<SubGraph.AuthGraph>(
                startDestination = RegisterScreen,
            ) {
                composable<RegisterScreen> {
                    RegisterScreen(
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateToLoginScreen = {
                            navController.navigate(route = LoginScreen) {
                                popUpTo(RegisterScreen) {
                                    inclusive = true
                                }
                            }
                        },
                        viewModel = viewModel,
                        preferenceDataStore = preferenceDataStore,
                        dataStoreManager = dataStoreManager
                    )
                }

                composable<LoginScreen> {
                    LoginScreen(
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateToPasswordListScreen = {
                            navController.navigate(route = SubGraph.HomeGraph) {
                                popUpTo(SubGraph.AuthGraph)
                            }
                            viewModel.authPasswordState = ""
                        },
                        promptManager = promptManager,
                        viewModel = viewModel,
                        dataStoreManager = dataStoreManager
                    )
                }
            }

            navigation<SubGraph.HomeGraph>(startDestination = PasswordListScreen) {

                composable<SettingsScreen> {
                    SettingsScreen(
                        dataStoreManager = dataStoreManager,
                        animatedVisibilityScope = this,
                        navigateBackToPasswordListScreen = {
                            navController.navigateUp()
                        },
                        navigateBackToRegisterScreen = {
                            navController.navigate(SubGraph.AuthGraph) {
                                popUpTo(SubGraph.HomeGraph)
                            }
                        }
                    )
                }

                composable<PasswordListScreen> {
                    PasswordList(
                        placeHolderSize = animatedSize,
                        modifier = modifier,
                        navigateToPasswordDetailScreenWithIdValue = {
                            navController.navigate(PasswordDetailScreen(id = it))
                        },
                        navigateToPasswordDetailScreenWithIdAs0 = {

                            navController.navigate(PasswordDetailScreen(id = 0)) {
                                viewModel.passwordTitleState = ""
                                viewModel.passwordUserNameState = ""
                                viewModel.passwordPasswordState = ""
                                viewModel.passwordNoteState = ""
                                viewModel.textFieldEnabled = true
                                viewModel.cardExpanded = false
                            }
                        },
                        openFab = {
                            navController.navigate(route = SettingsScreen)
                        },
                        viewModel = viewModel,
                        animatedVisibilityScope = this
                    )
                }

                composable<PasswordDetailScreen> {

                    val arguments = it.toRoute<PasswordDetailScreen>()

                    PasswordDetailScreen(
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateBackToPasswordListScreen = {
                            navController.navigateUp()
                        },
                        viewModel = viewModel,
                        placeHolderSize = animatedSize,
                        id = arguments.id
                    )
                }
            }
        }
    }

}