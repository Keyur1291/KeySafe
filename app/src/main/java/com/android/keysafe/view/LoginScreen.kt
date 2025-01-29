package com.android.keysafe.view

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.window.core.layout.WindowWidthSizeClass
import com.android.keysafe.view.components.BiometricPromptManager
import com.android.keysafe.view.components.BiometricPromptManager.BiometricResult
import com.android.keysafe.data.database.auth.DataStoreManager


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navigateToPasswordListScreen: () -> Unit,
    promptManager: BiometricPromptManager,
    dataStoreManager: DataStoreManager
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val biometricResult by promptManager.promptResult.collectAsState(initial = null)
    biometricResult?.let { result ->
        when (result) {
            is BiometricResult.AuthenticationError -> result.error
            BiometricResult.AuthenticationFailed -> "Authentication failed"
            BiometricResult.AuthenticationNotSet -> "Authentication not set"
            BiometricResult.AuthenticationSuccess -> navigateToPasswordListScreen()

            BiometricResult.FeatureUnavailable -> "Feature unavailable"
            BiometricResult.HardwareUnavailable -> "Hardware unavailable"
        }
    }
    val context = LocalContext.current

    val resultCode by remember { mutableIntStateOf(Int.MIN_VALUE) }
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            when (resultCode) {
                1 -> {
                    Toast.makeText(context, "Fingerprint enrolled Successfully", Toast.LENGTH_LONG)
                        .show()
                }

                2 -> {
                    Toast.makeText(context, "Fingerprint enrollment rejected", Toast.LENGTH_LONG)
                        .show()
                }

                else -> {
                    Toast.makeText(context, "Fingerprint enrollment canceled", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )
    val color = MaterialTheme.colorScheme.primary

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

    with(sharedTransitionScope) {

        Scaffold { it ->

            Surface(
                modifier = modifier.fillMaxSize()
            ) {
                when (windowSizeClass.windowWidthSizeClass) {

                    WindowWidthSizeClass.EXPANDED -> {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .sharedBounds(
                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedContentState = rememberSharedContentState(key = "registerButton"),
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = 0.8f,
                                            stiffness = 280f
                                        )
                                    }
                                ),
                        ) {
                            LoginTopContent(
                                modifier = Modifier.weight(0.1f),
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            Spacer(Modifier.height(700.dp))
                            LoginBottomContent(
                                animatedVisibilityScope = animatedVisibilityScope,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(1f)
                                    .fillMaxWidth(0.6f),
                                navigateToPasswordListScreen = navigateToPasswordListScreen,
                                promptManager = promptManager,
                                dataStoreManager = dataStoreManager,
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    }

                    WindowWidthSizeClass.MEDIUM -> {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .animateContentSize()
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .sharedBounds(
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedContentState = rememberSharedContentState(key = "registerButton"),
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = 0.8f,
                                            stiffness = 280f
                                        )
                                    }
                                ),
                        ) {
                            LoginTopContent(
                                modifier = Modifier.weight(0.1f),
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            Spacer(Modifier.height(100.dp))
                            LoginBottomContent(
                                animatedVisibilityScope = animatedVisibilityScope,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(0.7f)
                                    .windowInsetsPadding(WindowInsets.navigationBars),
                                navigateToPasswordListScreen = navigateToPasswordListScreen,
                                promptManager = promptManager,
                                dataStoreManager = dataStoreManager,
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    }

                    else -> {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .sharedBounds(
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedContentState = rememberSharedContentState(key = "registerButton"),
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = 0.8f,
                                            stiffness = 380f
                                        )
                                    }
                                )
                                .animateContentSize()
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        ) {
                            LoginTopContent(
                                modifier = Modifier,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            Spacer(Modifier.height(100.dp))
                            LoginBottomContent(
                                animatedVisibilityScope = animatedVisibilityScope,
                                modifier = Modifier.fillMaxWidth(0.8f),
                                navigateToPasswordListScreen = navigateToPasswordListScreen,
                                promptManager = promptManager,
                                dataStoreManager = dataStoreManager,
                                sharedTransitionScope = sharedTransitionScope
                            )
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginTopContent(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val color = MaterialTheme.colorScheme.primary

    with(sharedTransitionScope) {
        Canvas(
            modifier = modifier
                .sharedElement(
                    state = rememberSharedContentState("Logo"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth()
                .height(250.dp)
        ) {
            val width = this.size.width
            val height = this.size.height

            // Create a Path for the rectangle with an increased curve
            val path = Path().apply {
                moveTo(0f, 0f) // Top-left corner
                lineTo(width, 0f) // Top-right corner
                lineTo(width, height) // Right side slightly above bottom-right corner
                quadraticTo(
                    width / 2, height + 500f, // Control point for a deeper curve
                    0f, height  // End at the bottom-left corner
                )
                close() // Complete the path
            }

            // Draw the path
            drawPath(
                path = path,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginBottomContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navigateToPasswordListScreen: () -> Unit,
    promptManager: BiometricPromptManager,
    dataStoreManager: DataStoreManager
) {

    val fragmentActivity = LocalActivity.current as FragmentActivity

    val savedPassword by dataStoreManager.getFromDataStore().collectAsState(initial = null)

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility
    val hapticFeedbackManager = LocalHapticFeedback.current

    var passwordState by remember { mutableStateOf("") }
    var supportingText by remember { mutableStateOf("") }
    var textFieldError by remember { mutableStateOf(false) }

    with(sharedTransitionScope) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState("introTitle"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
                style = MaterialTheme.typography.displayMedium,
                text = "Login"
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState("regTextField"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth(),
                isError = textFieldError,
                supportingText = { Text(text = supportingText) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (passwordState == "") {
                            supportingText = "Please enter the password"
                            textFieldError = true
                        } else if (passwordState == savedPassword!!.loginPassword) {
                            textFieldError = false
                            navigateToPasswordListScreen()
                        } else {
                            supportingText = "Password does not match"
                            textFieldError = true
                        }
                    }
                ),
                shape = RoundedCornerShape(15.dp),
                value = passwordState,
                onValueChange = { passwordState = it },
                label = { Text(text = "Password") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisibility = !passwordVisibility
                            hapticFeedbackManager.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
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
                val isBiometricEnabled = savedPassword?.biometricEnable ?: false
                if (isBiometricEnabled) {
                    OutlinedButton(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {

                            hapticFeedbackManager.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )

                            promptManager.showBiometricPrompt(
                                fragmentActivity = fragmentActivity,
                                title = "Enter your screen lock",
                                description = "To access your passwords, KeySafe needs to make sure it's you"
                            )
                        }
                    ) {
                        Text(text = "Biometrics")
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Button(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState("regLoginBtn"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {

                        hapticFeedbackManager.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )

                        if (passwordState == "") {

                            supportingText = "Please enter the password"
                            textFieldError = true

                        } else if (passwordState == savedPassword!!.loginPassword) {

                            textFieldError = false
                            navigateToPasswordListScreen()
                            passwordState = ""

                        } else {

                            supportingText = "Wrong Password"
                            textFieldError = true
                        }
                    }
                ) {
                    Text(text = "Login")
                }
            }
        }
    }

}