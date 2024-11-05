package com.android.keysafe.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.keysafe.Navigation.RegisterScreen
import com.android.keysafe.ViewModel.PasswordViewModel
import com.android.keysafe.R
import com.android.keysafe.data.DataStoreManager
import com.android.keysafe.data.Password
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PasswordList(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PasswordViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    dataStoreManager: DataStoreManager
) {

    val passwordsList = viewModel.getPasswords.collectAsState(initial = listOf())

    Scaffold(
        modifier = Modifier
            .sharedBounds(
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                placeHolderSize = placeHolderSize,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedContentState = rememberSharedContentState(key = "main"),
                boundsTransform = { _, _ ->
                    spring(
                        dampingRatio = 0.9f,
                        stiffness = 380f
                    )
                }
            ),
        topBar = {
            SearchBar(
                viewModel = viewModel,
                navController = navController
            )
        },
        floatingActionButton = {
            FabUI(dataStoreManager = dataStoreManager, navController = navController)
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            if (passwordsList.value.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        route = com.android.keysafe.Navigation.PasswordDetailScreen(
                                            id = 0,
                                        )
                                    ) {
                                        viewModel.passwordTitleState = ""
                                        viewModel.passwordUserNameState = ""
                                        viewModel.passwordPasswordState = ""
                                        viewModel.passwordNoteState = ""
                                        viewModel.textFieldEnabled = true
                                        viewModel.cardExpanded = false
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .sharedBounds(
                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                    placeHolderSize = placeHolderSize,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedContentState = rememberSharedContentState(key = "bounds"),
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = 0.9f,
                                            stiffness = 380f
                                        )
                                    }
                                ),
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(8.dp),
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Add a password",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    items(passwordsList.value, key = { password -> password.id }) { password ->
                        SwipeToDeleteContainer(
                            placeHolderSize = placeHolderSize,
                            animatedVisibilityScope = animatedVisibilityScope,
                            password = password,
                            navController = navController,
                            onDelete = {
                                viewModel.deletePassword(password)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    route = com.android.keysafe.Navigation.PasswordDetailScreen(
                                        id = 0
                                    )
                                ) {
                                    viewModel.passwordTitleState = ""
                                    viewModel.passwordUserNameState = ""
                                    viewModel.passwordPasswordState = ""
                                    viewModel.passwordNoteState = ""
                                    viewModel.textFieldEnabled = true
                                    viewModel.cardExpanded = false
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .sharedBounds(
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedContentState = rememberSharedContentState(key = "bounds"),
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.9f,
                                        stiffness = 380f
                                    )
                                }
                            ),
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(8.dp),
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Add a password",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                            painter = painterResource(R.drawable.ic_undraw_empty_street_sfxm),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FabUI(dataStoreManager: DataStoreManager, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        var showSubMenu by remember { mutableStateOf(false) }
        val transition = updateTransition(targetState = showSubMenu, label = "FabTransition")
        val rotation by transition.animateFloat(label = "rotation"){ if(it) 315f else 0f }
        val scope = rememberCoroutineScope()
        val menuItems = listOf(
            MenuItem(
                icon = Icons.Rounded.Password,
                title = "Change Password",
                onClick = {
                    scope.launch {
                        dataStoreManager.clearDataStore()
                        navController.navigate(route = RegisterScreen)
                    }
                }
            ),
            MenuItem(
                icon = Icons.AutoMirrored.Rounded.Help,
                title = "Help",
                onClick = {  }
            )
        )

        AnimatedVisibility(
            visible = showSubMenu,
            enter = fadeIn() + slideInVertically(
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 180f),
                initialOffsetY = {it}
            ) + expandVertically(),
            exit = fadeOut() + slideOutVertically(
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 180f),
                targetOffsetY = {it}
            ) + shrinkVertically()
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(menuItems) {
                    MenuUI(
                        menuItems = it
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                showSubMenu = !showSubMenu
            }
        ) {
            Icon(
                modifier = Modifier.rotate(rotation),
                imageVector = Icons.Rounded.Add,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {

}

@Composable
fun MenuUI(menuItems: MenuItem) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Spacer(Modifier.width(8.dp))
        Text(text = menuItems.title)
        SmallFloatingActionButton(
            onClick = menuItems.onClick
        ) {
            Icon(
                imageVector = menuItems.icon,
                contentDescription = menuItems.title
            )
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PasswordItem(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    modifier: Modifier,
    password: Password,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PasswordViewModel
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
            .background(MaterialTheme.colorScheme.surface)
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
    navController: NavController,
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
                        onClick = {
                            onDelete(password)
                        }
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
                    viewModel = viewModel,
                    placeHolderSize = placeHolderSize,
                    modifier = Modifier,
                    animatedVisibilityScope = animatedVisibilityScope,
                    password = password,
                    onClick = {
                        val id = password.id
                        navController.navigate(
                            com.android.keysafe.Navigation.PasswordDetailScreen(
                                id = id
                            )
                        ) {
                            launchSingleTop = true
                            viewModel.passwordTitleState = password.title
                            viewModel.passwordUserNameState = password.userName
                            viewModel.passwordPasswordState = password.password
                            viewModel.passwordNoteState = password.note
                            viewModel.textFieldEnabled = false
                            viewModel.cardExpanded = false
                        }
                    }
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