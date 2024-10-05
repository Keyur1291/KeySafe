package com.android.keysafe

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.android.keysafe.BiometricPromptManager.BiometricResult
import java.util.regex.Pattern


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.LoginScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navController: NavController,
    promptManager: BiometricPromptManager,
    viewModel: PasswordViewModel
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val biometricResult by promptManager.promptResult.collectAsState(initial = null)
    biometricResult?.let { result ->
        when (result) {
            is BiometricResult.AuthenticationError -> result.error
            BiometricResult.AuthenticationFailed -> "Authentication failed"
            BiometricResult.AuthenticationNotSet -> "Authentication not set"
            BiometricResult.AuthenticationSuccess -> {
                navController.navigate(route = PasswordListScreen)
            }

            BiometricResult.FeatureUnavailable -> "Feature unavailable"
            BiometricResult.HardwareUnavailable -> "Hardware unavailable"
        }
    }
    val context = LocalContext.current
    val resultCode by remember { mutableIntStateOf(Int.MIN_VALUE) }
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            when(resultCode) {
                1 -> {
                    Toast.makeText(context, "Fingerprint enrolled Successfully", Toast.LENGTH_LONG).show()
                }

                2 -> {
                    Toast.makeText(context, "Fingerprint enrollment rejected", Toast.LENGTH_LONG).show()
                }

                else -> {
                    Toast.makeText(context, "Fingerprint enrollment canceled", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }

    Scaffold { innerPadding ->

        when (windowSizeClass.windowWidthSizeClass) {

            WindowWidthSizeClass.EXPANDED -> {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                        .sharedBounds(
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedContentState = rememberSharedContentState(key = "main"),
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = 0.8f,
                                    stiffness = 280f
                                )
                            }
                        ),
                ) {
                    TopContent(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                    )
                    BottomContent(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        navController = navController,
                        promptManager = promptManager,
                        viewModel = viewModel
                    )
                }
            }

            WindowWidthSizeClass.MEDIUM -> {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .animateContentSize()
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                        .sharedBounds(
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedContentState = rememberSharedContentState(key = "main"),
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = 0.8f,
                                    stiffness = 280f
                                )
                            }
                        ),
                ) {
                    TopContent(modifier = Modifier.fillMaxWidth(0.6f))
                    Spacer(Modifier.height(10.dp))
                    BottomContent(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        navController = navController,
                        promptManager = promptManager,
                        viewModel = viewModel
                    )
                }
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .animateContentSize()
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                        .sharedBounds(
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedContentState = rememberSharedContentState(key = "main"),
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = 0.8f,
                                    stiffness = 380f
                                )
                            }
                        ),
                ) {
                    TopContent()
                    Spacer(Modifier.height(10.dp))
                    BottomContent(
                        modifier = Modifier,
                        navController = navController,
                        promptManager = promptManager,
                        viewModel = viewModel
                    )
                }
            }
        }

    }
}

@Composable
fun TopContent(modifier: Modifier = Modifier) {

    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_undraw_unlock_24mb),
        contentDescription = null
    )
}

@Composable
fun BottomContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    promptManager: BiometricPromptManager,
    viewModel: PasswordViewModel
) {

    val fragmentActivity = LocalContext.current as FragmentActivity

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility
    val passwordPattern = Pattern.compile("^" + "(?=.*[!@#\$%^&*()_+{}|<>?:;,.])" + "(?=\\S+$)" + ".{6,}" + "$");
    var supportingText by remember { mutableStateOf("") }
    var textFieldError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.displayMedium,
            text = "Login"
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            isError = textFieldError,
            supportingText = { Text(text = supportingText) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (viewModel.authPasswordState == "") {
                        supportingText = "Please enter the password"
                        textFieldError = true
                    } else if (viewModel.authPasswordState.matches(passwordPattern.toRegex())) {
                        textFieldError = false
                        navController.navigate(route = PasswordListScreen)
                    } else {
                        supportingText = "Password does not match"
                        textFieldError = true
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            value = viewModel.authPasswordState,
            onValueChange = {  viewModel.onAuthPasswordChange(it) },
            label = { Text(text = "Password") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (!passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    promptManager.showBiometricPrompt(
                        fragmentActivity = fragmentActivity,
                        title = "Enter your screen lock",
                        description = "To access your passwords, KeySafe needs to make sure it's you"
                    )
                }
            ) {
                Text(text = "Use biometrics")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    if (viewModel.authPasswordState == "") {
                        supportingText = "Please enter the password"
                        textFieldError = true
                    } else if (viewModel.authPasswordState.matches(passwordPattern.toRegex())) {
                        textFieldError = false
                        navController.navigate(route = PasswordListScreen)
                    } else {
                        supportingText = "Password does not match"
                        textFieldError = true
                    }
                }
            ) {
                Text(text = "Login")
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPrev() {
    TopContent()
}