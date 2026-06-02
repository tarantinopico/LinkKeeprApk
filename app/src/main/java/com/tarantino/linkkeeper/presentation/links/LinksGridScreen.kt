package com.tarantino.linkkeeper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksGridScreen(
    groupId: Long, 
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: MainViewModel = hiltViewModel()
) {
    var groupName by remember { mutableStateOf<String?>("Links") }
    var links by remember { mutableStateOf<List<SavedLink>>(emptyList()) }

    LaunchedEffect(groupId) {
        viewModel.groupsWithCount.value.find { it.group.id == groupId }?.let {
            groupName = it.group.name
        }
        viewModel.getLinksByGroup(groupId).collect { 
            links = it
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GlassTopAppBar(
                title = { Text(groupName ?: "Links") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (links.isEmpty()) {
            EmptyState(
                icon = Icons.Default.List,
                title = "No links yet",
                subtitle = "Save some links to this group and they will appear here.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(160.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 24.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = innerPadding.calculateBottomPadding() + 48.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(links.size) { index ->
                    val link = links[index]
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 50L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(iosFade) + slideInVertically(iosSpringOffset) { it / 2 }
                    ) {
                        LinkCard(
                            link = link,
                            onOpen = { viewModel.openLink(link.url) },
                            onCopy = { viewModel.copyLink(link.url) },
                            onToggleRead = { viewModel.toggleRead(link.id, !link.isRead) },
                            onDelete = { viewModel.deleteLink(link.id) }
                        )
                    }
                }
            }
        }
    }
}
