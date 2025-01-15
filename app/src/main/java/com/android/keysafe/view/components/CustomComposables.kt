package com.android.keysafe.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.android.keysafe.PasswordViewModel
import com.android.keysafe.model.Password
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    viewModel: PasswordViewModel,
    navigateToPasswordDetailScreenWithIdValue: (id: Int) -> Unit,
) {

    val passwordsList = viewModel.getPasswords.collectAsState(initial = listOf())
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        SearchBar(
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(0.4f)
            ),
            modifier = Modifier,
            inputField = {
                SearchBarDefaults.InputField(
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

                items(passwordsList.value.filter { it.doesMatchSearchQuery(searchText) }) { password ->
                    SearchItem(
                        password = password,
                        onClick = {
                            navigateToPasswordDetailScreenWithIdValue(password.id)
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .sharedBounds(
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                sharedContentState = rememberSharedContentState(key = "expandFab"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
        onClick = onClick
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
    val icon: ImageVector = when (password.title) {

        "Facebook" -> Icons.Rounded.Facebook
        "Instagram" -> Icons.Rounded.Facebook
        "Twitter" -> Icons.Rounded.Facebook
        "X" -> Icons.Rounded.Facebook
        else -> Icons.Rounded.Lock

    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onClick()
                }
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
            ),
    ) {
        Icon(
            modifier = Modifier.padding(8.dp),
            imageVector = icon,
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
    viewModel: PasswordViewModel
) {

    var showDialog by remember { mutableStateOf(false) }
    var isDeleted by remember { mutableStateOf(false) }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isDeleted = true
                false
            } else {
                false
            }
        },
        positionalThreshold = { width -> width * 0.6f }
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
        enter = expandVertically(
            animationSpec = tween(durationMillis = animationDuration),
            expandFrom = Alignment.Top
        ) + fadeIn(),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
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
                        viewModel.passwordTitleState = password.title
                        viewModel.passwordUserNameState = password.userName
                        viewModel.passwordPasswordState = password.password
                        viewModel.passwordNoteState = password.note
                        viewModel.textFieldEnabled = false
                        viewModel.cardExpanded = false
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