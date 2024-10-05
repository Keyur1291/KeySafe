package com.android.keysafe

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.android.keysafe.Navigation.NavController
import com.android.keysafe.ViewModel.PasswordViewModel
import com.android.keysafe.ui.theme.KeySafeTheme

class MainActivity : FragmentActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    private val viewModel by viewModels<PasswordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeySafeTheme {
                Surface {
                    NavController(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout),
                        promptManager = promptManager,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KeySafeTheme {  }
}