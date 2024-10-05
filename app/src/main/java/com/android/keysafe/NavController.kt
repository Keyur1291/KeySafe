package com.android.keysafe

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavController(
    modifier: Modifier = Modifier,
    promptManager: BiometricPromptManager,
    viewModel: PasswordViewModel
) {

    SharedTransitionLayout {

        val navController = rememberNavController()
        val animatedSize = SharedTransitionScope.PlaceHolderSize.animatedSize

        NavHost(navController = navController, startDestination = LoginScreen) {

            composable<LoginScreen> {
                LoginScreen(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    promptManager = promptManager,
                    viewModel = viewModel
                )
            }

            composable<PasswordListScreen> {
                PasswordList(
                    animatedVisibilityScope = this,
                    modifier = modifier,
                    navController = navController,
                    viewModel = viewModel,
                    placeHolderSize = animatedSize
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