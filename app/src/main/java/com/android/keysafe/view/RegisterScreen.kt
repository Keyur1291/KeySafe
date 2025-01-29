package com.android.keysafe.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.window.core.layout.WindowWidthSizeClass
import com.android.keysafe.data.database.auth.DataStoreManager
import com.android.keysafe.data.database.auth.DataStoreManager.Companion.BIOMETRIC
import com.android.keysafe.data.database.auth.DataStoreManager.Companion.PASSWORD
import com.android.keysafe.data.model.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RegisterScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
    preferenceDataStore: DataStore<Preferences>,
    dataStoreManager: DataStoreManager
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val scope = rememberCoroutineScope()
    var isPasswordSaved by remember { mutableStateOf(false) }
    val onSaveSuccess = { isPasswordSaved = true }

    LaunchedEffect(key1 = Unit) {
        checkIsPasswordSaved(preferenceDataStore) {
            isPasswordSaved = it
        }
    }

    if (isPasswordSaved) {
        navigateToLoginScreen()
    }

    with(sharedTransitionScope) {
        Scaffold { innerPadding ->

            when (windowSizeClass.windowWidthSizeClass) {

                WindowWidthSizeClass.EXPANDED -> {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        RegisterTopContent(
                            modifier = Modifier.weight(0.1f),
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        Spacer(Modifier.height(700.dp))
                        RegisterBottomContent(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            navigateToLoginScreen = navigateToLoginScreen,
                            scope = scope,
                            dataStoreManager = dataStoreManager
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
                            .verticalScroll(rememberScrollState()),
                    ) {
                        RegisterTopContent(
                            modifier = Modifier.weight(0.1f),
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        Spacer(Modifier.height(100.dp))
                        RegisterBottomContent(
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier.fillMaxWidth(0.6f),
                            navigateToLoginScreen = navigateToLoginScreen,
                            scope = scope,
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
                            .animateContentSize()
                            .fillMaxSize()
                            .padding()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        RegisterTopContent(
                            modifier = Modifier.fillMaxWidth(),
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        Spacer(Modifier.height(150.dp))
                        RegisterBottomContent(
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.8f),
                            navigateToLoginScreen = navigateToLoginScreen,
                            scope = scope,
                            dataStoreManager = dataStoreManager,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                }
            }
        }
    }


}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RegisterTopContent(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
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
                .height(100.dp)
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

suspend fun checkIsPasswordSaved(
    preferenceDataStore: DataStore<Preferences>,
    onResult: (Boolean) -> Unit
) {
    val preferences = preferenceDataStore.data.first()
    val password = preferences[PASSWORD]
    val biometric = preferences[BIOMETRIC]
    val isPasswordSaved = password != null
    onResult(isPasswordSaved)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RegisterBottomContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
    scope: CoroutineScope,
    dataStoreManager: DataStoreManager,
) {

    val hapticFeedbackManager = LocalHapticFeedback.current
    var biometricBoolean by remember { mutableStateOf(false) }
    var passwordState by remember { mutableStateOf("") }
    var confPasswordState by remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility

    var confPasswordVisibility by remember { mutableStateOf(false) }
    val confIcon =
        if (confPasswordVisibility) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility

    val passwordPattern =
        Pattern.compile("^" + "(?=.*[!@#\$%^&*()_+{}|<>?:;,.])" + "(?=\\S+$)" + ".{6,}" + "$");

    var supportingText by remember { mutableStateOf("") }
    var textFieldError by remember { mutableStateOf(false) }

    var confSupportingText by remember { mutableStateOf("") }
    var confTextFieldError by remember { mutableStateOf(false) }


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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (passwordState == "") {

                            supportingText = "Please enter the password"
                            textFieldError = true

                        } else if (passwordState.matches(passwordPattern.toRegex())) {

                            textFieldError = false
                            supportingText = ""

                        } else {

                            supportingText = "Password does not meet the minimum requirements"
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
                        imageVector = if(passwordVisibility) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
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
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                isError = confTextFieldError,
                supportingText = { Text(text = confSupportingText) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (confPasswordState == "") {

                            confSupportingText = "Please enter the password"
                            confTextFieldError = true

                        } else if (confPasswordState == passwordState) {

                            confTextFieldError = false
                            confSupportingText = ""

                        } else {

                            confSupportingText = "Password does not match"
                            confTextFieldError = true
                        }
                    }
                ),
                shape = RoundedCornerShape(15.dp),
                value = confPasswordState,
                onValueChange = { confPasswordState = it },
                label = { Text(text = "Confirm Password") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = if(passwordVisibility) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            confPasswordVisibility = !confPasswordVisibility
                            hapticFeedbackManager.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                        }
                    ) {
                        Icon(imageVector = confIcon, contentDescription = null)
                    }
                },
                visualTransformation = if (!confPasswordVisibility) PasswordVisualTransformation() else VisualTransformation.None
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Enable Biometric Auth"
                )
                Switch(
                    checked = biometricBoolean,
                    onCheckedChange = {
                        biometricBoolean = !biometricBoolean
                        hapticFeedbackManager.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState("regLoginBtn"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    if (passwordState == "") {

                        supportingText = "Please enter the password"
                        textFieldError = true

                    } else if (!passwordState.matches(passwordPattern.toRegex())) {

                        textFieldError = true
                        supportingText = "Password does not meet the minimum requirement"

                    } else if (confPasswordState == "") {

                        confSupportingText = "Please confirm the password"
                        confTextFieldError = true
                    } else if (
                        passwordState.matches(passwordPattern.toRegex()) &&
                        passwordState == confPasswordState
                    ) {

                        textFieldError = false
                        confTextFieldError = false
                        supportingText = ""
                        confSupportingText = ""

                        scope.launch {
                            dataStoreManager.saveToDataStore(
                                Auth(
                                    loginPassword = passwordState,
                                    biometricEnable = biometricBoolean
                                )
                            )
                        }
                        navigateToLoginScreen()
                    } else {
                        confSupportingText = "Password does not match"
                        confTextFieldError = true
                    }
                }
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Create Password"
                )
            }
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(textFieldError){
                Text(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    text = "You need to create a password to be able to log in to the app"
                )
            }
        }
    }
}