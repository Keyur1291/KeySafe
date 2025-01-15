package com.android.keysafe

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.android.keysafe.view.components.BiometricPromptManager
import com.android.keysafe.model.DataStoreManager
import com.android.keysafe.model.preferenceDataStore
import com.android.keysafe.navController.Destinations.PasswordDetailScreen
import com.android.keysafe.navController.NavController
import com.android.keysafe.ui.theme.KeySafeTheme
import com.android.keysafe.view.components.CustomSearchBar
import com.android.keysafe.view.components.ExpandableFab
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

class MainActivity : FragmentActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    private val viewModel by viewModels<PasswordViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge(

        )
        installSplashScreen()
        setContent {

            val context = LocalContext.current
            val dataStoreManager = DataStoreManager(context = context)
            val navController = rememberNavController()

            KeySafeTheme {
                Scaffold { innerPaddings ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                    ) {
                        NavController(
                            navController = navController,
                            paddingValues = PaddingValues(
                                top = innerPaddings.calculateTopPadding(),
                                bottom = innerPaddings.calculateBottomPadding()
                            ),
                            modifier = Modifier,
                            promptManager = promptManager,
                            viewModel = viewModel,
                            dataStoreManager = dataStoreManager,
                            preferenceDataStore = preferenceDataStore
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KeySafeTheme { }
}