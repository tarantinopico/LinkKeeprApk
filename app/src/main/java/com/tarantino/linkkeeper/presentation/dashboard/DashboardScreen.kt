package com.tarantino.linkkeeper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen(
    onNavigateToGroupManagement: () -> Unit,
    onNavigateToGroup: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: MainViewModel = hiltViewModel()
) {
    val groups by viewModel.groupsWithCount.collectAsState()
    val recentLinks by viewModel.recentLinks.collectAsState()

    var showAddLink by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GlassTopAppBar(
                title = { Text("Keepr", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onNavigateToGroupManagement) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLink = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 32.dp,
                bottom = innerPadding.calculateBottomPadding() + 100.dp
            ),
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            item {
                Text(
                    text = "Your Groups",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groups) { group ->
                        GroupCard(groupWithCount = group, onClick = { onNavigateToGroup(group.group.id) })
                    }
                }
            }

            item {
                Text(
                    text = "Recent Links",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                )
            }

            item {
                val links = recentLinks.take(10)
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.height((links.size.coerceAtMost(5) * 116 + 32).dp)
                ) {
                    items(links) { link ->
                        LinkCardCompact(
                            link = link,
                            onOpen = { viewModel.openLink(link.url) },
                            onCopy = { viewModel.copyLink(link.url) }
                        )
                    }
                }
            }
        }
    }

    if (showAddLink) {
        AddLinkBottomSheet(onDismiss = { showAddLink = false })
    }
}
