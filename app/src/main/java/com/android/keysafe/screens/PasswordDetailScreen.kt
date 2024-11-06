package com.android.keysafe.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.keysafe.viewModel.PasswordViewModel
import com.android.keysafe.data.Password
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PasswordDetailScreen(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    animatedVisibilityScope: AnimatedVisibilityScope,
    id: Int,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PasswordViewModel
) {


    val passwordPattern = Pattern.compile("^" + "(?=.*[!@#\$%^&*()_+{}|<>?:;,.])" + "(?=\\S+$)" + ".{6,}" + "$");
    val scope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    var titleSupportingText by remember { mutableStateOf("") }
    var titleFieldError by remember { mutableStateOf(false) }

    var userNameSupportingText by remember { mutableStateOf("") }
    var userNameFieldError by remember { mutableStateOf(false) }

    var passwordSupportingText by remember { mutableStateOf("") }
    var passwordFieldError by remember { mutableStateOf(false) }


    var passwordVisibility by remember { mutableStateOf(false) }
    val icon =
        if (passwordVisibility) Icons.Rounded.Visibility
        else Icons.Outlined.VisibilityOff

    val cardIcon =
        if (viewModel.cardExpanded) Icons.Rounded.ExpandLess
        else Icons.Rounded.ExpandMore

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id == 0) "Add password" else "Update password",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()

                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .sharedBounds(
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    placeHolderSize = placeHolderSize,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedContentState = rememberSharedContentState(key = if (id == 0) "bounds" else viewModel.passwordTitleState),
                    boundsTransform = { _, _ ->
                        spring(
                            dampingRatio = 0.9f,
                            stiffness = 380f
                        )
                    }
                )
                .skipToLookaheadSize(),
        ) {
            OtherTextField(
                modifier = Modifier
                    .height(100.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "title/${viewModel.passwordTitleState}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            spring(
                                dampingRatio = 0.9f,
                                stiffness = 380f
                            )
                        }
                    ),
                value = viewModel.passwordTitleState,
                onValueChange = { viewModel.onTitleChange(it) },
                label = if (viewModel.textFieldEnabled || viewModel.passwordTitleState.isEmpty()) "Title" else "",
                singleLine = true,
                enabled = viewModel.textFieldEnabled,
                visualTransformation = VisualTransformation.None,
                icon = Icons.Rounded.ContentCopy,
                imeAction = ImeAction.Next,
                supportingText = titleSupportingText,
                isError = titleFieldError,
                keyboardType = KeyboardType.Text,
                clipString = { clipboardManager.setText(AnnotatedString(viewModel.passwordTitleState)) }
            )

            OtherTextField(
                modifier = Modifier
                    .height(100.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "userName/${viewModel.passwordUserNameState}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            spring(
                                dampingRatio = 0.9f,
                                stiffness = 380f
                            )
                        }
                    )
                    .skipToLookaheadSize(),
                value = viewModel.passwordUserNameState,
                onValueChange = { viewModel.onUserNameChange(it) },
                label = if (viewModel.textFieldEnabled || viewModel.passwordUserNameState.isEmpty()) "Username" else "",
                singleLine = true,
                enabled = viewModel.textFieldEnabled,
                visualTransformation = VisualTransformation.None,
                icon = Icons.Rounded.ContentCopy,
                imeAction = ImeAction.Next,
                supportingText = userNameSupportingText,
                isError = userNameFieldError,
                keyboardType = KeyboardType.Email,
                clipString = { clipboardManager.setText(AnnotatedString(viewModel.passwordUserNameState)) }
            )

            PasswordTextField(
                modifier = Modifier
                    .height(100.dp)
                    .skipToLookaheadSize(),
                value = viewModel.passwordPasswordState,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = if (viewModel.textFieldEnabled || viewModel.passwordPasswordState.isEmpty()) "Password" else "",
                singleLine = true,
                enabled = viewModel.textFieldEnabled,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                icon = icon,
                icon2 = Icons.Rounded.ContentCopy,
                passwordVisibility = { passwordVisibility = !passwordVisibility },
                imeAction = ImeAction.Next,
                supportingText = passwordSupportingText,
                isError = passwordFieldError,
                keyboardType = KeyboardType.Password,
                clipString = { clipboardManager.setText(AnnotatedString(viewModel.passwordPasswordState)) }
            )

            Spacer(Modifier.height(10.dp))

            OtherTextField(
                modifier = Modifier.skipToLookaheadSize(),
                value = viewModel.passwordNoteState,
                onValueChange = { viewModel.onNoteChange(it) },
                label = if (viewModel.textFieldEnabled || viewModel.passwordNoteState.isEmpty()) "Note" else "",
                enabled = viewModel.textFieldEnabled,
                minLines = 3,
                singleLine = false,
                visualTransformation = VisualTransformation.None,
                icon = Icons.Rounded.ContentCopy,
                imeAction = ImeAction.Default,
                supportingText = "",
                isError = false,
                keyboardType = KeyboardType.Text,
                clipString = { clipboardManager.setText(AnnotatedString(viewModel.passwordNoteState)) }
            )

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .skipToLookaheadSize(),
            ) {

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    shape = RoundedCornerShape(15.dp),
                    onClick = {

                        if (viewModel.passwordTitleState.isEmpty()) {

                            titleSupportingText = "Please enter the title"
                            titleFieldError = true

                        } else if (viewModel.passwordUserNameState.isEmpty()) {

                            userNameSupportingText = "Please enter the username"
                            userNameFieldError = true

                        } else if (viewModel.passwordPasswordState.isEmpty()) {

                            passwordSupportingText = "Please enter the password"
                            passwordFieldError = true

                        } else if (viewModel.passwordPasswordState.matches(passwordPattern.toRegex())) {

                            if (id != 0) {
                                //Update the password of current id if id != 0
                                viewModel.upsertPassword(
                                    password = Password(
                                        id = id,
                                        title = viewModel.passwordTitleState.trim(),
                                        userName = viewModel.passwordUserNameState.trim(),
                                        password = viewModel.passwordPasswordState.trim(),
                                        note = viewModel.passwordNoteState.trim()
                                    )
                                )
                                scope.launch {
                                    navController.navigateUp()
                                }
                            } else {
                                //Create a new Password if id = 0
                                viewModel.upsertPassword(
                                    password = Password(
                                        title = viewModel.passwordTitleState.trim(),
                                        userName = viewModel.passwordUserNameState.trim(),
                                        password = viewModel.passwordPasswordState.trim(),
                                        note = viewModel.passwordNoteState.trim()
                                    )
                                )
                                scope.launch {
                                    navController.navigateUp()
                                }
                            }

                        } else {
                            passwordSupportingText = "Please enter a valid password"
                            passwordFieldError = true
                        }
                    }) {
                    Text(text = "Save")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    shape = RoundedCornerShape(15.dp),
                    onClick = { viewModel.textFieldEnabled = !viewModel.textFieldEnabled }
                ) {
                    if (viewModel.textFieldEnabled) {
                        Text(text = "Read mode")
                    } else {
                        Text(text = "Edit mode")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.skipToLookaheadSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                viewModel.cardExpanded = !viewModel.cardExpanded
                            }
                            .padding(8.dp),
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            text = "Advance options"
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = cardIcon,
                            contentDescription = null
                        )
                    }
                    AnimatedVisibility(viewModel.cardExpanded) {
                        Column {
                            val hapticFeedbackManager = LocalHapticFeedback.current
                            Text(text = "Length: ${viewModel.length}")
                            Slider(
                                value = viewModel.length.toFloat(),
                                onValueChange = {
                                    viewModel.length = it.roundToInt()
                                    hapticFeedbackManager.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove,
                                    )
                                },
                                valueRange = 1f..50f,
                                steps = 50,
                                onValueChangeFinished = {
                                    hapticFeedbackManager.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove,
                                    )
                                }
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = viewModel.includeLower,
                                    onCheckedChange = {
                                        viewModel.includeLower = !viewModel.includeLower
                                        hapticFeedbackManager.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    },
                                    enabled = false
                                )
                                Text(text = "Include lower alphabets")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = viewModel.includeDigit,
                                    onCheckedChange = {
                                        viewModel.includeDigit = !viewModel.includeDigit
                                        hapticFeedbackManager.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    }
                                )
                                Text(text = "Include digits")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = viewModel.includeUpper,
                                    onCheckedChange = {
                                        viewModel.includeUpper = !viewModel.includeUpper
                                        hapticFeedbackManager.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    }
                                )
                                Text(text = "Include upper alphabets")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = viewModel.includeSpecialChars,
                                    onCheckedChange = {
                                        viewModel.includeSpecialChars =
                                            !viewModel.includeSpecialChars
                                        hapticFeedbackManager.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove,
                                        )
                                    }
                                )
                                Text(text = "Include special characters")
                            }
                            Button(
                                shape = RoundedCornerShape(15.dp),
                                onClick = {
                                    viewModel.passwordPasswordState = viewModel.generatePassword(
                                        length = viewModel.length,
                                        includeLower = viewModel.includeLower,
                                        includeUpper = viewModel.includeUpper,
                                        includeDigits = viewModel.includeDigit,
                                        includeSpecialChars = viewModel.includeSpecialChars
                                    )
                                }
                            ) {
                                Text(text = "Autogenerate password")
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    singleLine: Boolean,
    minLines: Int = 1,
    visualTransformation: VisualTransformation,
    icon: ImageVector,
    icon2: ImageVector,
    passwordVisibility: () -> Unit,
    imeAction: ImeAction,
    isError: Boolean,
    supportingText: String,
    keyboardType: KeyboardType,
    clipString: () -> Unit

) {

    OutlinedTextField(
        supportingText = { Text(text = supportingText) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledBorderColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        enabled = enabled,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        trailingIcon = {
            Row {
                IconButton(
                    onClick = passwordVisibility
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
                IconButton(
                    onClick = clipString
                ) {
                    Icon(imageVector = icon2, contentDescription = null)
                }
            }
        }
    )
}

@Composable
fun OtherTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    singleLine: Boolean,
    minLines: Int = 1,
    visualTransformation: VisualTransformation,
    icon: ImageVector,
    imeAction: ImeAction,
    isError: Boolean,
    supportingText: String,
    keyboardType: KeyboardType,
    clipString: () -> Unit

) {

    OutlinedTextField(
        supportingText = { Text(text = supportingText) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledBorderColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        enabled = enabled,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        trailingIcon = {
            IconButton(
                onClick = clipString
            ) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    )
}


@Composable
fun Grid(modifier: Modifier = Modifier) {

    var txt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it }
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it },
                    minLines = 3
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {}
            ) {
                Text(text = "Btn")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {}
            ) {
                Text(text = "Btn")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {}
            ) {
                Text(text = "Btn")
            }
        }
    }
}

@Preview(
    showSystemUi = true, showBackground = true,
    device = "spec:id=reference_foldable,shape=Normal,width=673,height=841,unit=dp,dpi=420"
)
@Composable
private fun DetailsPreview() {

    Grid()

}