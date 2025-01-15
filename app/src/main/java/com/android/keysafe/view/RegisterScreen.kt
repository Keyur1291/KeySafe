package com.android.keysafe.view

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.window.core.layout.WindowWidthSizeClass
import com.android.keysafe.PasswordViewModel
import com.android.keysafe.model.DataStoreManager
import com.android.keysafe.model.DataStoreManager.Companion.BIOMETRIC
import com.android.keysafe.model.DataStoreManager.Companion.PASSWORD
import com.android.keysafe.model.LoginPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RegisterScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
    viewModel: PasswordViewModel,
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
                        .verticalScroll(rememberScrollState()),
                ) {
                    TopContent(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                    )
                    RegisterBottomContent(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        navigateToLoginScreen = navigateToLoginScreen,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
                        animatedVisibilityScope = animatedVisibilityScope
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
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    TopContent(modifier = Modifier.fillMaxWidth(0.6f))
                    Spacer(Modifier.height(10.dp))
                    RegisterBottomContent(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        navigateToLoginScreen = navigateToLoginScreen,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
                        animatedVisibilityScope = animatedVisibilityScope
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
                        .verticalScroll(rememberScrollState()),
                ) {
                    TopContent()
                    Spacer(Modifier.height(10.dp))
                    RegisterBottomContent(
                        modifier = Modifier,
                        navigateToLoginScreen = navigateToLoginScreen,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
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
fun SharedTransitionScope.RegisterBottomContent(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel,
    navigateToLoginScreen: () -> Unit,
    scope: CoroutineScope,
    dataStoreManager: DataStoreManager,
) {

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


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.displaySmall,
            text = "Create Password"
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
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
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
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
            trailingIcon = {
                IconButton(
                    onClick = {
                        confPasswordVisibility = !confPasswordVisibility
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
                .fillMaxWidth(0.9f)
                .padding(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Enable Biometric Auth"
            )
            Switch(
                checked = biometricBoolean,
                onCheckedChange = { biometricBoolean = !biometricBoolean }
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("registerButton"),
                    animatedVisibilityScope = animatedVisibilityScope
                    ),
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
                            LoginPassword(
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
                text = "Create"
            )
        }
    }
}