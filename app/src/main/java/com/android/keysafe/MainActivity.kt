package com.android.keysafe

import android.content.Context
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.android.keysafe.data.database.auth.DataStoreManager
import com.android.keysafe.data.database.auth.preferenceDataStore
import com.android.keysafe.data.database.password.PassPhrase
import com.android.keysafe.data.database.password.PasswordDatabase
import com.android.keysafe.ui.theme.KeySafeTheme
import com.android.keysafe.view.components.BiometricPromptManager
import com.android.keysafe.view.navigation.NavController
import com.android.keysafe.viewmodel.MainViewModel
import net.sqlcipher.database.SupportFactory

class MainActivity : FragmentActivity() {


    private fun provideUserDatabasePassphrase(context: Context) = PassPhrase(context)

    private fun provideSupportFactory(passPhrase: PassPhrase) = SupportFactory(passPhrase.getPassphrase())

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    val database by lazy {
        Room.databaseBuilder(
            this,
            PasswordDatabase::class.java,
            "passwords.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val mainViewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(database.passwordDao) as T
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                darkScrim = Color.Transparent.value.toInt(),
                scrim = Color.Transparent.value.toInt()
            )
        )
        installSplashScreen()
        setContent {

            val context = LocalContext.current
            val dataStoreManager = DataStoreManager(context = context)
            val navController = rememberNavController()
            val passwordState by mainViewModel.passwordState.collectAsState()

            KeySafeTheme {
                Scaffold { innerPaddings ->
                    Surface(
                        modifier = Modifier.padding()
                    ) {
                        NavController(
                            modifier = Modifier,
                            paddingValues = innerPaddings,
                            promptManager = promptManager,
                            passwordState = passwordState,
                            preferenceDataStore = preferenceDataStore,
                            dataStoreManager = dataStoreManager,
                            navController = navController,
                            onPasswordEvent = mainViewModel::onEvent
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KeySafeTheme { }
}