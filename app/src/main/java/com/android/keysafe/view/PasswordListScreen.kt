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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.keysafe.R
import com.android.keysafe.di.PasswordEvent
import com.android.keysafe.di.PasswordState
import com.android.keysafe.view.components.CustomSearchBar
import com.android.keysafe.view.components.ExpandableFab
import com.android.keysafe.view.components.SwipeToDeleteContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun PasswordList(
    sharedTransitionScope: SharedTransitionScope,
    placeHolderSize: SharedTransitionScope.PlaceHolderSize,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navigateToPasswordDetailScreenWithIdValue: (id: Int) -> Unit,
    navigateToPasswordDetailScreenWithIdAs0: () -> Unit,
    openFab: () -> Unit,
    onPasswordEvent: (PasswordEvent) -> Unit,
    passwordState: PasswordState,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val hapticFeedbackManager = LocalHapticFeedback.current
    val hazeState = remember { HazeState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                CustomSearchBar(
                    childModifier = Modifier.clip(RoundedCornerShape(15.dp)),
                    navigateToPasswordDetailScreenWithIdValue = navigateToPasswordDetailScreenWithIdValue,
                    passwordState = passwordState,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            },
            floatingActionButton = {
                ExpandableFab(
                    modifier = Modifier.padding(paddingValues),
                    animatedVisibilityScope = animatedVisibilityScope
                ) {
                    openFab()
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = modifier
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    if (passwordState.passwordsList.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = innerPadding,
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
                                            hapticFeedbackManager.performHapticFeedback(
                                                HapticFeedbackType.LongPress
                                            )
                                            navigateToPasswordDetailScreenWithIdAs0()
                                        }
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 8.dp
                                        ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(15.dp))
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
                            items(
                                passwordState.passwordsList,
                                key = { password -> password.id }) { password ->
                                SwipeToDeleteContainer(
                                    onPasswordEvent = onPasswordEvent,
                                    placeHolderSize = placeHolderSize,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    password = password,
                                    navigateBackToPasswordScreenWithIdValue = {
                                        navigateToPasswordDetailScreenWithIdValue(
                                            password.id
                                        )
                                    },
                                    onDelete = {
                                        onPasswordEvent(PasswordEvent.DeletePassword(password))
                                    },
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
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
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(15.dp))
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

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
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
    }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {

}