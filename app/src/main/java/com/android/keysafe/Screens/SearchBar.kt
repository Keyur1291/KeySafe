package com.android.keysafe.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.keysafe.Navigation.PasswordDetailScreen
import com.android.keysafe.ViewModel.PasswordViewModel
import com.android.keysafe.data.Password

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    viewModel: PasswordViewModel,
    navController: NavController
) {

    val passwordsList = viewModel.getPasswords.collectAsState(initial = listOf())
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
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
                        } else if(!isSearching) {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
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
                            navController.navigate(route = PasswordDetailScreen(id = password.id,))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchItem(password: Password, onClick: () -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SearchPrev() {

    //SearchBar()

}