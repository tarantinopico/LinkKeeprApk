package com.tarantino.linkkeeper

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LinksGridScreen(groupId: Long, onBack: () -> Unit, viewModel: MainViewModel = hiltViewModel()) {
    var groupName by remember { mutableStateOf<String?>("Links") }
    var links by remember { mutableStateOf<List<SavedLink>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(groupId) {
        viewModel.groupsWithCount.value.find { it.group.id == groupId }?.let {
            groupName = it.group.name
        }
        viewModel.getLinksByGroup(groupId).collect { 
            links = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                icon = Icons.Default.Home, // Placeholder for empty state icon since we don't have it imported here. Wait, let's just use it anyway.
                title = "No links yet",
                subtitle = "Save some links to this group and they will appear here.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(links, key = { it.id }) { link ->
                    LinkCard(
                        link = link,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        onToggleRead = { viewModel.toggleRead(link.id, !link.isRead) },
                        onDelete = { viewModel.deleteLink(link.id) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}
