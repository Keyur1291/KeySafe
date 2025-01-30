package com.android.keysafe.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PersonOutline
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
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.keysafe.data.model.Password
import com.android.keysafe.di.PasswordEvent
import com.android.keysafe.di.PasswordState
import com.android.keysafe.view.components.OtherTextField
import com.android.keysafe.view.components.PasswordTextField
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PasswordDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    animatedVisibilityScope: AnimatedVisibilityScope,
    id: Int,
    modifier: Modifier = Modifier,
    navigateBackToPasswordListScreen: () -> Unit,
    passwordState: PasswordState,
    onPasswordEvent: (PasswordEvent) -> Unit
) {

    val hapticFeedbackManager = LocalHapticFeedback.current
    val passwordPattern =
        Pattern.compile("^" + "(?=.*[!@#\$%^&*()_+{}|<>?:;,.])" + "(?=\\S+$)" + ".{6,}" + "$")
    val scope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    var titleSupportingText by remember { mutableStateOf("") }
    var titleFieldError by remember { mutableStateOf(false) }

    var userNameSupportingText by remember { mutableStateOf("") }
    var userNameFieldError by remember { mutableStateOf(false) }

    var passwordSupportingText by remember { mutableStateOf("") }
    var passwordFieldError by remember { mutableStateOf(false) }

    var textFieldEnabled by remember { mutableStateOf(false) }

    //Advance Option Card ValuesCard
    var cardExpanded by remember { mutableStateOf(false) }
    val cardIcon =
        if (cardExpanded) Icons.Rounded.ExpandLess
        else Icons.Rounded.ExpandMore
    var passwordLength by remember { mutableIntStateOf(10) }
    var includeDigit by remember { mutableStateOf(false) }
    val includeLower by remember { mutableStateOf(true) }
    var includeUpper by remember { mutableStateOf(false) }
    var includeSpecialChars by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    val icon =
        if (passwordVisibility) Icons.Rounded.Visibility
        else Icons.Outlined.VisibilityOff
    val color = MaterialTheme.colorScheme.surfaceVariant


    with(sharedTransitionScope) {

        Scaffold(
            topBar = {

                MediumTopAppBar(
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState("DetailLogo"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .shadow(42.dp, clip = false)
                        .drawBehind {
                            val width = this.size.width
                            val height = this.size.height

                            // Create a Path for the rectangle with an increased curve
                            val path = Path().apply {
                                moveTo(0f, 0f) // Top-left corner
                                lineTo(width, 0f) // Top-right corner
                                lineTo(
                                    width,
                                    height
                                ) // Right side slightly above bottom-right corner
                                quadraticTo(
                                    width / 2, height + 50f, // Control point for a deeper curve
                                    0f, height  // End at the bottom-left corner
                                )
                                close() // Complete the path
                            }

                            // Draw the path
                            drawPath(
                                path = path,
                                color = color
                            )
                        },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = color),
                    title = {
                        Text(
                            modifier = Modifier,
                            text = if (id == 0) "Add password" else "Update password",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                hapticFeedbackManager.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                                navigateBackToPasswordListScreen()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->

            Surface(
                modifier = modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .sharedBounds(
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                            placeHolderSize = placeHolderSize,
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedContentState = rememberSharedContentState(key = if (id == 0) "newPassword" else passwordState.title),
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = 0.9f,
                                    stiffness = 380f
                                )
                            }
                        )
                        .skipToLookaheadSize()
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(start = 16.dp, end = 16.dp)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                        .windowInsetsPadding(WindowInsets.navigationBars),
                ) {
                    OtherTextField(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .sharedElement(
                                state = rememberSharedContentState(key = "title/${passwordState.title}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.9f,
                                        stiffness = 380f
                                    )
                                }
                            )
                            .height(100.dp),
                        value = passwordState.title,
                        onValueChange = { onPasswordEvent(PasswordEvent.SetTitle(it)) },
                        label = "Title",
                        singleLine = true,
                        enabled = textFieldEnabled,
                        visualTransformation = VisualTransformation.None,
                        icon = Icons.Rounded.ContentCopy,
                        leadingIcon = Icons.Outlined.Apps,
                        imeAction = ImeAction.Next,
                        supportingText = titleSupportingText,
                        isError = titleFieldError,
                        keyboardType = KeyboardType.Text,
                        clipString = { clipboardManager.setText(AnnotatedString(passwordState.title)) }
                    )

                    OtherTextField(
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(key = "userName/${passwordState.userName}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.9f,
                                        stiffness = 380f
                                    )
                                }
                            )
                            .height(100.dp),
                        value = passwordState.userName,
                        onValueChange = { onPasswordEvent(PasswordEvent.SetUserName(it)) },
                        label = "Username",
                        singleLine = true,
                        enabled = textFieldEnabled,
                        visualTransformation = VisualTransformation.None,
                        leadingIcon = Icons.Outlined.PersonOutline,
                        icon = Icons.Rounded.ContentCopy,
                        imeAction = ImeAction.Next,
                        supportingText = userNameSupportingText,
                        isError = userNameFieldError,
                        keyboardType = KeyboardType.Email,
                        clipString = { clipboardManager.setText(AnnotatedString(passwordState.userName)) }
                    )

                    PasswordTextField(
                        modifier = Modifier
                            .height(100.dp),
                        value = passwordState.password,
                        onValueChange = {
                            onPasswordEvent(PasswordEvent.SetPassword(it))
                        },
                        label = "Password",
                        singleLine = true,
                        enabled = textFieldEnabled,
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        icon = icon,
                        icon2 = Icons.Rounded.ContentCopy,
                        leadingIcon = if (passwordVisibility) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        passwordVisibility = { passwordVisibility = !passwordVisibility },
                        imeAction = ImeAction.Next,
                        supportingText = passwordSupportingText,
                        isError = passwordFieldError,
                        keyboardType = KeyboardType.Password,
                        clipString = { clipboardManager.setText(AnnotatedString(passwordState.password)) }
                    )

                    Spacer(Modifier.height(10.dp))

                    OtherTextField(
                        value = passwordState.note,
                        onValueChange = { onPasswordEvent(PasswordEvent.SetNote(it)) },
                        label = "Note",
                        enabled = textFieldEnabled,
                        minLines = 3,
                        singleLine = false,
                        visualTransformation = VisualTransformation.None,
                        leadingIcon = Icons.Outlined.EditNote,
                        icon = Icons.Rounded.ContentCopy,
                        imeAction = ImeAction.Default,
                        supportingText = "",
                        isError = false,
                        keyboardType = KeyboardType.Text,
                        clipString = { clipboardManager.setText(AnnotatedString(passwordState.note)) }
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = modifier
                            .fillMaxWidth()
                            .skipToLookaheadSize(),
                    ) {

                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = {

                                hapticFeedbackManager.performHapticFeedback(
                                    HapticFeedbackType.LongPress,
                                )

                                clipboardManager.setText(
                                    AnnotatedString(
                                        "Title: ${passwordState.title.trim()}\n" +
                                                "Username: ${passwordState.userName.trim()}\n" +
                                                "Password: ${passwordState.password.trim()}\n" +
                                                if (passwordState.note.isNotEmpty()) {
                                                    "Note: ${passwordState.note.trim()}"
                                                } else ""
                                    )
                                )
                            }
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

                                hapticFeedbackManager.performHapticFeedback(
                                    HapticFeedbackType.LongPress,
                                )

                                if (passwordState.title.isEmpty()) {

                                    titleSupportingText = "Please enter the title"
                                    titleFieldError = true

                                } else if (passwordState.userName.isEmpty()) {

                                    userNameSupportingText = "Please enter the username"
                                    userNameFieldError = true

                                } else if (passwordState.password.isEmpty()) {

                                    passwordSupportingText = "Please enter the password"
                                    passwordFieldError = true

                                } else if (passwordState.password.matches(passwordPattern.toRegex())) {

                                    if (id != 0) {
                                        //Update the password of current id if id != 0
                                        onPasswordEvent(
                                            PasswordEvent.SavePassword(
                                                password = Password(
                                                    id = id,
                                                    title = passwordState.title.trim(),
                                                    userName = passwordState.userName.trim(),
                                                    password = passwordState.password.trim(),
                                                    note = passwordState.note.trim()
                                                )
                                            )
                                        )
                                        onPasswordEvent(PasswordEvent.SetTitle(""))
                                        onPasswordEvent(PasswordEvent.SetUserName(""))
                                        onPasswordEvent(PasswordEvent.SetPassword(""))
                                        onPasswordEvent(PasswordEvent.SetNote(""))
                                        scope.launch {
                                            navigateBackToPasswordListScreen()
                                        }
                                    } else {
                                        //Create a new Password if id = 0
                                        onPasswordEvent(
                                            PasswordEvent.SavePassword(
                                                password = Password(
                                                    title = passwordState.title.trim(),
                                                    userName = passwordState.userName.trim(),
                                                    password = passwordState.password.trim(),
                                                    note = passwordState.note.trim()
                                                )
                                            )
                                        )
                                        onPasswordEvent(PasswordEvent.SetTitle(""))
                                        onPasswordEvent(PasswordEvent.SetUserName(""))
                                        onPasswordEvent(PasswordEvent.SetPassword(""))
                                        onPasswordEvent(PasswordEvent.SetNote(""))
                                        scope.launch {
                                            navigateBackToPasswordListScreen()
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
                            onClick = {
                                textFieldEnabled = !textFieldEnabled
                                hapticFeedbackManager.performHapticFeedback(
                                    HapticFeedbackType.LongPress,
                                )
                            }
                        ) {
                            if (textFieldEnabled) {
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
                        modifier = Modifier
                            .skipToLookaheadSize()
                            .imePadding()
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
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        cardExpanded = !cardExpanded
                                        hapticFeedbackManager.performHapticFeedback(
                                            HapticFeedbackType.LongPress,
                                        )
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
                            AnimatedVisibility(cardExpanded) {
                                Column {
                                    Text(text = "Length: $passwordLength")
                                    Slider(
                                        value = passwordLength.toFloat(),
                                        onValueChange = {
                                            passwordLength = it.roundToInt()
                                            hapticFeedbackManager.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )
                                        },
                                        valueRange = 1f..20f,
                                        steps = 20,
                                        onValueChangeFinished = {
                                            hapticFeedbackManager.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )
                                        }
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = includeLower,
                                            onCheckedChange = {
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            },
                                            enabled = false
                                        )
                                        Text(text = "Include lower alphabets")
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                includeDigit = !includeDigit
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                            .padding(end = 12.dp)
                                    ) {
                                        Checkbox(
                                            checked = includeDigit,
                                            onCheckedChange = {
                                                includeDigit = !includeDigit
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                        )
                                        Text(text = "Include digits")
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                includeUpper = !includeUpper
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                            .padding(end = 12.dp)
                                    ) {
                                        Checkbox(
                                            checked = includeUpper,
                                            onCheckedChange = {
                                                includeUpper = !includeUpper
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                        )
                                        Text(text = "Include upper alphabets")
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                includeSpecialChars = !includeSpecialChars
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                            .padding(end = 12.dp)
                                    ) {
                                        Checkbox(
                                            checked = includeSpecialChars,
                                            onCheckedChange = {
                                                includeSpecialChars = !includeSpecialChars
                                                hapticFeedbackManager.performHapticFeedback(
                                                    HapticFeedbackType.LongPress,
                                                )
                                            }
                                        )
                                        Text(text = "Include special characters")
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        modifier = Modifier,
                                        shape = RoundedCornerShape(15.dp),
                                        onClick = {
                                            passwordVisibility = true

                                            hapticFeedbackManager.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )

                                            onPasswordEvent(
                                                PasswordEvent.SetPassword(
                                                    passwordValue = passwordState.generatePassword(
                                                        length = passwordLength,
                                                        includeLower = includeLower,
                                                        includeUpper = includeUpper,
                                                        includeDigits = includeDigit,
                                                        includeSpecialChars = includeSpecialChars
                                                    )
                                                )
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
    }
}


@Composable
fun Grid(modifier: Modifier = Modifier) {

    var txt by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
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
)
@Composable
private fun DetailsPreview() {

    Grid()

}