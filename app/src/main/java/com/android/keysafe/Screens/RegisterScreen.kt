package com.android.keysafe.Screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.android.keysafe.Navigation.LoginScreen
import com.android.keysafe.Navigation.PasswordListScreen
import com.android.keysafe.ViewModel.PasswordViewModel
import com.android.keysafe.data.DataStoreManager
import com.android.keysafe.data.DataStoreManager.Companion.PASSWORD
import com.android.keysafe.data.LoginPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RegisterScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PasswordViewModel,
    preferenceDataStore: DataStore<Preferences>,
    dataStoreManager: DataStoreManager
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val scope = rememberCoroutineScope()
    var isPasswordSaved by remember { mutableStateOf(false) }
    val onSaveSuccess = { isPasswordSaved = true }

    LaunchedEffect(key1 = Unit) {
        checkIsPasswordSaved(preferenceDataStore) { it ->
            isPasswordSaved = it
        }
    }

    if (isPasswordSaved) {
        navController.navigate(route = LoginScreen)
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
                    RegisterBottomContent(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        navController = navController,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
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
                    RegisterBottomContent(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        navController = navController,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
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
                    RegisterBottomContent(
                        modifier = Modifier,
                        navController = navController,
                        viewModel = viewModel,
                        scope = scope,
                        dataStoreManager = dataStoreManager,
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
    val isPasswordSaved = password != null
    onResult(isPasswordSaved)
}

@Composable
fun RegisterBottomContent(
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel,
    navController: NavController,
    scope: CoroutineScope,
    dataStoreManager: DataStoreManager,
) {

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
                    if (viewModel.savePasswordState == "") {
                        supportingText = "Please enter the password"
                        textFieldError = true
                    } else if (viewModel.savePasswordState.matches(passwordPattern.toRegex())) {
                        textFieldError = false
                    } else {
                        supportingText = "Password does not match"
                        textFieldError = true
                    }
                }
            ),
            shape = RoundedCornerShape(15.dp),
            value = viewModel.savePasswordState,
            onValueChange = { viewModel.onSavePasswordChange(it) },
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
                    if (viewModel.confSavePasswordState == "") {
                        confSupportingText = "Please enter the password"
                        confTextFieldError = true
                    } else if (viewModel.confSavePasswordState == viewModel.savePasswordState) {
                        confTextFieldError = false
                    } else {
                        confSupportingText = "Password does not match"
                        confTextFieldError = true
                    }
                }
            ),
            shape = RoundedCornerShape(15.dp),
            value = viewModel.confSavePasswordState,
            onValueChange = { viewModel.onConfSavePasswordChange(it) },
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
        Button(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(15.dp),
            onClick = {
                if (viewModel.savePasswordState == "") {
                    supportingText = "Please enter the password"
                    textFieldError = true
                } else if (viewModel.confSavePasswordState == "") {
                    confSupportingText = "Please confirm the password"
                    confTextFieldError = true
                } else if (
                    viewModel.savePasswordState.matches(passwordPattern.toRegex()) &&
                    viewModel.savePasswordState == viewModel.confSavePasswordState
                ) {
                    textFieldError = false
                    confTextFieldError = false
                    scope.launch {
                        dataStoreManager.saveToDataStore(
                            LoginPassword(
                                loginPassword = viewModel.savePasswordState
                            )
                        )
                    }
                    navController.navigate(route = PasswordListScreen)
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