package com.android.keysafe.view

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.keysafe.data.database.auth.DataStoreManager
import com.android.keysafe.data.model.Auth
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    sharedTransitionScope: SharedTransitionScope,
    navigateBackToPasswordListScreen: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    dataStoreManager: DataStoreManager,
    navigateBackToRegisterScreen: () -> Unit
) {

    val hapticFeedbackManager = LocalHapticFeedback.current
    val haze = remember { HazeState() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            text = { Text(text = "Are you sure want to delete this password") },
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row {
                    Button(
                        onClick = {
                            hapticFeedbackManager.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                            scope.launch { dataStoreManager.clearDataStore() }
                            showDialog = false
                            navigateBackToRegisterScreen()
                        }
                    ) {
                        Text(text = "Yes")
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            hapticFeedbackManager.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                            showDialog = false
                        }
                    ) {
                        Text(text = "No")
                    }
                }
            }
        )
    }

    val savedPassword by dataStoreManager.getFromDataStore().collectAsState(initial = null)
    val loginPassword = savedPassword?.loginPassword
    var biometricBoolean = savedPassword?.biometricEnable ?: false
    val biometricStatus = if (biometricBoolean) "Enabled" else "Disabled"
    val menuItems = listOf(
        SettingItem(
            "Unlock with biometric", biometricStatus, Icons.Rounded.Fingerprint, onClick = {
                biometricBoolean = !biometricBoolean
                hapticFeedbackManager.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
                scope.launch {
                    dataStoreManager.saveToDataStore(
                        Auth(
                            loginPassword.toString(),
                            biometricBoolean
                        )
                    )
                }
            }, modifier = Modifier
        ),
        SettingItem(
            "Reset Password", "Delete the old password and set a new one.", Icons.Rounded.Settings,
            onClick = {
                showDialog = true
                hapticFeedbackManager.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
            },
            modifier = Modifier
        )
    )

    with(sharedTransitionScope) {

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.hazeEffect(haze),
                    title = {
                        Text(
                            text = "Settings",
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
                    }
                )
            }
        ) { innerPadding ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .sharedBounds(
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                            sharedContentState = rememberSharedContentState(key = "expandFab"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                ) {
                    LazyColumn(
                        modifier = Modifier.skipToLookaheadSize()
                    ) {
                        items(menuItems) { item ->
                            SettingsMenuItem(
                                settingItem = item
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SettingItem(
    val title: String,
    val description: String,
    val imageVector: ImageVector,
    val onClick: () -> Unit,
    val modifier: Modifier
)

@Composable
fun SettingsMenuItem(
    modifier: Modifier = Modifier,
    settingItem: SettingItem
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                settingItem.onClick()
            }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = settingItem.imageVector,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = settingItem.title
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = settingItem.description
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun SettingPreview() {

    val settingItem = SettingItem(
        "Biometric",
        "Enable",
        Icons.Rounded.Fingerprint,
        onClick = {},
        modifier = Modifier
    )

}