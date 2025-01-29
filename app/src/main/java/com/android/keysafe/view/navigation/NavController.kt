package com.android.keysafe.view.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.android.keysafe.data.database.auth.DataStoreManager
import com.android.keysafe.di.PasswordEvent
import com.android.keysafe.di.PasswordState
import com.android.keysafe.navController.Destinations.LoginScreen
import com.android.keysafe.navController.Destinations.PasswordDetailScreen
import com.android.keysafe.navController.Destinations.PasswordListScreen
import com.android.keysafe.navController.Destinations.RegisterScreen
import com.android.keysafe.navController.Destinations.SettingsScreen
import com.android.keysafe.navController.SubGraph
import com.android.keysafe.view.LoginScreen
import com.android.keysafe.view.PasswordDetailScreen
import com.android.keysafe.view.PasswordList
import com.android.keysafe.view.RegisterScreen
import com.android.keysafe.view.SettingsScreen
import com.android.keysafe.view.components.BiometricPromptManager


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavController(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    promptManager: BiometricPromptManager,
    passwordState: PasswordState,
    preferenceDataStore: DataStore<Preferences>,
    dataStoreManager: DataStoreManager,
    navController: NavHostController,
    onPasswordEvent: (PasswordEvent) -> Unit
) {

    SharedTransitionLayout {

        val animatedSize = SharedTransitionScope.PlaceHolderSize.animatedSize

        NavHost(
            navController = navController,
            startDestination = SubGraph.AuthGraph,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    initialOffset = { it }
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    targetOffset = { -it }
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    initialOffset = { it }
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300, easing = LinearEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    targetOffset = { -it }
                )
            }
        ) {

            navigation<SubGraph.AuthGraph>(startDestination = RegisterScreen,) {
                composable<RegisterScreen> {
                    RegisterScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateToLoginScreen = {
                            navController.navigate(route = LoginScreen) {
                                popUpTo(RegisterScreen) {
                                    inclusive = true
                                }
                            }
                        },
                        preferenceDataStore = preferenceDataStore,
                        dataStoreManager = dataStoreManager
                    )
                }

                composable<LoginScreen> {
                    LoginScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateToPasswordListScreen = {
                            navController.navigate(route = SubGraph.HomeGraph) {
                                popUpTo(SubGraph.AuthGraph)
                            }
                        },
                        promptManager = promptManager,
                        dataStoreManager = dataStoreManager
                    )
                }
            }

            navigation<SubGraph.HomeGraph>(startDestination = PasswordListScreen) {

                composable<SettingsScreen> {
                    SettingsScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
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
                        sharedTransitionScope = this@SharedTransitionLayout,
                        paddingValues = paddingValues,
                        passwordState = passwordState,
                        placeHolderSize = animatedSize,
                        modifier = modifier,
                        navigateToPasswordDetailScreenWithIdValue = {
                            navController.navigate(PasswordDetailScreen(id = it))
                        },
                        navigateToPasswordDetailScreenWithIdAs0 = {

                            navController.navigate(PasswordDetailScreen(id = 0)) {
                                onPasswordEvent(PasswordEvent.SetTitle(""))
                                onPasswordEvent(PasswordEvent.SetUserName(""))
                                onPasswordEvent(PasswordEvent.SetPassword(""))
                                onPasswordEvent(PasswordEvent.SetNote(""))
                            }
                        },
                        openFab = {
                            navController.navigate(route = SettingsScreen)
                        },
                        animatedVisibilityScope = this,
                        onPasswordEvent = onPasswordEvent,
                    )
                }

                composable<PasswordDetailScreen> {

                    val arguments = it.toRoute<PasswordDetailScreen>()

                    PasswordDetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onPasswordEvent = onPasswordEvent,
                        passwordState = passwordState,
                        animatedVisibilityScope = this,
                        modifier = modifier,
                        navigateBackToPasswordListScreen = {
                            navController.navigateUp()
                        },
                        placeHolderSize = animatedSize,
                        id = arguments.id
                    )
                }
            }
        }
    }
}