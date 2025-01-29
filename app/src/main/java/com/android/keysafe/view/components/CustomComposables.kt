package com.android.keysafe.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.android.keysafe.R
import com.android.keysafe.data.model.Password
import com.android.keysafe.di.PasswordEvent
import com.android.keysafe.di.PasswordState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    navigateToPasswordDetailScreenWithIdValue: (id: Int) -> Unit,
    passwordState: PasswordState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val paddings  by animateDpAsState(
        targetValue = if(!isSearching) 24.dp else 0.dp,
        animationSpec = tween(),
        label = "searchLabel"
    )
    val color = MaterialTheme.colorScheme.surfaceVariant

    Box (
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        with(sharedTransitionScope) {
            Canvas(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState("HomeLogo"),
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
                        width / 2, height + 80f, // Control point for a deeper curve
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
        SearchBar(
            shape = RoundedCornerShape(15.dp),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddings),
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = childModifier,
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onSearch = {
                        searchText = ""
                        isSearching = false
                        KeyboardOptions(
                            imeAction = ImeAction.Default
                        )
                    },
                    expanded = isSearching,
                    onExpandedChange = { isSearching = it },
                    placeholder = { Text(text = "Search Here") },
                    leadingIcon = {
                        if (isSearching) {
                            IconButton(
                                onClick = {
                                    isSearching = false
                                    searchText = ""
                                }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null
                            )
                        }
                    },
                    trailingIcon = {
                        if (isSearching && searchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchText = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                )
            },
            expanded = isSearching,
            onExpandedChange = { isSearching = it }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.displayCutout.asPaddingValues())
            ) {

                items(passwordState.passwordsList.filter { it.doesMatchSearchQuery(searchText) }) { password ->
                    SearchItem(
                        modifier = Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        bringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        password = password,
                        onClick = {
                            navigateToPasswordDetailScreenWithIdValue(password.id)
                            passwordState.title = password.title
                            passwordState.userName = password.userName
                            passwordState.password = password.password
                            passwordState.note = password.note
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SearchItem(
    password: Password, onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onClick()
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                text = password.title
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = password.userName
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExpandableFab(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {

    val hapticFeedbackManager = LocalHapticFeedback.current

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier
            .sharedBounds(
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                sharedContentState = rememberSharedContentState(key = "expandFab"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
        onClick = {
            hapticFeedbackManager.performHapticFeedback(
                HapticFeedbackType.LongPress
            )
            onClick()
        }
    ) {
        Icon(
            modifier = Modifier.sharedElement(
                rememberSharedContentState("settingsIcon"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            imageVector = Icons.Rounded.Settings,
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PasswordItem(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    modifier: Modifier,
    password: Password,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val icon: Int = when (password.title) {

        "Facebook" -> R.drawable.icons8_facebook
        "Instagram" -> R.drawable.icons8_instagram
        "Twitter" -> R.drawable.icons8_twitter__1_
        "X" -> R.drawable.icons8_twitter__1_
        "Telegram" -> R.drawable.icons8_telegram
        "Discord" -> R.drawable.icons8_discord
        "Snapchat" -> R.drawable.snapchat_logo_svgrepo_com
        "Amazon" -> R.drawable.icons8_amazon
        "Google" -> R.drawable.icons8_google
        "Github" -> R.drawable.github_svgrepo_com
        "LinkedIn" -> R.drawable.icons8_linkedin
        "Pinterest" -> R.drawable.icons8_pinterest
        "Tiktok" -> R.drawable.icons8_tiktok
        "Whatsapp" -> R.drawable.icons8_whatsapp
        "Tinder" -> R.drawable.icons8_tinder
        "Microsoft" -> R.drawable.icons8_microsoft
        "KeySafe" -> R.drawable.encrypted_24px
        else -> R.drawable.round_gpp_good_24

    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .sharedBounds(
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                placeHolderSize = placeHolderSize,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedContentState = rememberSharedContentState(key = password.title),
                boundsTransform = { _, _ ->
                    spring(
                        dampingRatio = 0.9f,
                        stiffness = 380f
                    )
                }
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = {
                    onClick()
                }
            )
            .padding(horizontal = 16.dp),
    ) {
        Image(
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(15.dp))
                .size(60.dp),
            painter = painterResource(icon),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                modifier = Modifier.sharedElement(
                    placeHolderSize = placeHolderSize,
                    state = rememberSharedContentState(key = "title/${password.title}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        spring(
                            dampingRatio = 0.9f,
                            stiffness = 380f
                        )
                    }
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                text = password.title
            )
            Text(
                modifier = Modifier.sharedElement(
                    placeHolderSize = placeHolderSize,
                    state = rememberSharedContentState(key = "userName/${password.userName}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        spring(
                            dampingRatio = 0.9f,
                            stiffness = 380f
                        )
                    }
                ),
                style = MaterialTheme.typography.bodySmall,
                text = password.userName
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SwipeToDeleteContainer(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    password: Password,
    onDelete: (Password) -> Unit,
    animationDuration: Int = 200,
    navigateBackToPasswordScreenWithIdValue: (id: Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPasswordEvent: (PasswordEvent) -> Unit
) {

    val hapticFeedBackManager = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }
    var isDeleted by remember { mutableStateOf(false) }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isDeleted = true
                hapticFeedBackManager.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
                false
            } else {
                false
            }
        },
        positionalThreshold = { width -> width * 2.3f },
    )
    if (showDialog) {
        AlertDialog(
            text = { Text(text = "Are you sure want to delete this password") },
            onDismissRequest = { showDialog = false; isDeleted = false },
            confirmButton = {
                Row {
                    Button(
                        onClick = { onDelete(password) }
                    ) {
                        Text(text = "Yes")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            showDialog = false
                            isDeleted = false
                        }
                    ) {
                        Text(text = "No")
                    }
                }
            }
        )
    }

    LaunchedEffect(key1 = isDeleted) {
        if (isDeleted) {
            showDialog = true
            delay(animationDuration.toLong())
        } else {
            showDialog = false
            delay(animationDuration.toLong())
        }
    }

    AnimatedVisibility(
        visible = !isDeleted,
        enter = slideInHorizontally (
            animationSpec = tween(durationMillis = animationDuration),
        ) + expandVertically(),
        exit = slideOutHorizontally(
            animationSpec = tween(durationMillis = animationDuration),
        ) + shrinkVertically()
    ) {
        SwipeToDismissBox(
            modifier = Modifier,
            state = swipeToDismissBoxState,
            backgroundContent = {
                DeleteBackground(
                    swipeToDismissBoxState,
                    animatedVisibilityScope
                )
            },
            content = {
                PasswordItem(
                    placeHolderSize = placeHolderSize,
                    modifier = Modifier,
                    password = password,
                    onClick = {
                        navigateBackToPasswordScreenWithIdValue(password.id)
                        onPasswordEvent(PasswordEvent.SetTitle(password.title))
                        onPasswordEvent(PasswordEvent.SetUserName(password.userName))
                        onPasswordEvent(PasswordEvent.SetPassword(password.password))
                        onPasswordEvent(PasswordEvent.SetNote(password.note))
                    },
                    animatedVisibilityScope = animatedVisibilityScope
                )
            },
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true
        )
    }
}

@Composable
fun DeleteBackground(
    swipeToDismissBoxState: SwipeToDismissBoxState,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val color =
        if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.surface
        }

    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            tint = MaterialTheme.colorScheme.onPrimary,
            imageVector = Icons.Rounded.DeleteSweep,
            contentDescription = null
        )
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
    leadingIcon: ImageVector,
    passwordVisibility: () -> Unit,
    imeAction: ImeAction,
    isError: Boolean,
    supportingText: String,
    keyboardType: KeyboardType,
    clipString: () -> Unit

) {

    val hapticFeedbackManager = LocalHapticFeedback.current

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        supportingText = { Text(text = supportingText) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
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
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = {
            Row {
                IconButton(
                    onClick = {
                        hapticFeedbackManager.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        passwordVisibility()
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
                IconButton(
                    onClick = {
                        hapticFeedbackManager.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        clipString()
                    }
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
    leadingIcon: ImageVector,
    imeAction: ImeAction,
    isError: Boolean,
    supportingText: String,
    keyboardType: KeyboardType,
    clipString: () -> Unit

) {

    val hapticFeedbackManager = LocalHapticFeedback.current
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        supportingText = { Text(text = supportingText) },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
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
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    hapticFeedbackManager.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    clipString()
                }
            ) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    )
}