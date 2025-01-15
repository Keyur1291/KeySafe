package com.android.keysafe.view

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat.Surface
import com.android.keysafe.PasswordViewModel
import com.android.keysafe.R
import com.android.keysafe.view.components.CustomSearchBar
import com.android.keysafe.view.components.ExpandableFab
import com.android.keysafe.view.components.SwipeToDeleteContainer
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.FluentMaterials
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)
@Composable
fun SharedTransitionScope.PasswordList(
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    modifier: Modifier = Modifier,
    navigateToPasswordDetailScreenWithIdValue: (id: Int) -> Unit,
    navigateToPasswordDetailScreenWithIdAs0: () -> Unit,
    openFab: () -> Unit,
    viewModel: PasswordViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val passwordsList = viewModel.getPasswords.collectAsState(initial = listOf())
    val hazeState = remember { HazeState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            CustomSearchBar(
                modifier = Modifier
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.ultraThin(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        inputScale = HazeInputScale.Auto
                        blurRadius = 100.dp
                        progressive = HazeProgressive.verticalGradient(
                            startIntensity = 0.2f, endIntensity = 0f
                        )
                    }
                    .background (Color.Transparent),
                viewModel = viewModel,
                navigateToPasswordDetailScreenWithIdValue = navigateToPasswordDetailScreenWithIdValue
            )
        },
        floatingActionButton = {
            ExpandableFab(
                animatedVisibilityScope = animatedVisibilityScope
            ) {
                openFab()
            }
        }
    ) { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = modifier
                    .hazeSource(hazeState)
                    .sharedBounds(
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedContentState = rememberSharedContentState(key = "loginButton"),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )
                    .fillMaxSize()
            ) {
                if (passwordsList.value.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = innerPadding,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .sharedBounds(
                                        placeHolderSize = placeHolderSize,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        sharedContentState = rememberSharedContentState(key = "newPassword"),
                                        boundsTransform = { _, _ ->
                                            spring(
                                                dampingRatio = 0.9f,
                                                stiffness = 380f
                                            )
                                        }
                                    )
                                    .fillMaxWidth()
                                    .clickable {
                                        navigateToPasswordDetailScreenWithIdAs0()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
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
                                viewModel = viewModel,
                                navigateBackToPasswordScreenWithIdValue = {
                                    navigateToPasswordDetailScreenWithIdValue(
                                        password.id
                                    )
                                },
                                onDelete = {
                                    viewModel.deletePassword(password)
                                },
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
                                .sharedBounds(
                                    placeHolderSize = placeHolderSize,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedContentState = rememberSharedContentState(key = "newPassword"),
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = 0.9f,
                                            stiffness = 380f
                                        )
                                    }
                                )
                                .fillMaxWidth()
                                .clickable {
                                    navigateToPasswordDetailScreenWithIdAs0()
                                }
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp),
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
                                modifier = Modifier,
                                painter = painterResource(R.drawable.ic_undraw_empty_street_sfxm),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {

}