package com.tarantino.linkkeeper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    onNavigateToGroupManagement: () -> Unit,
    onNavigateToGroup: (Long) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
    addLinkViewModel: AddLinkViewModel = hiltViewModel()
) {
    val groups by viewModel.groupsWithCount.collectAsState()
    val recentLinks by viewModel.recentLinks.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var urlInput by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GlassTopAppBar(
                title = { Text("Keepr", fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp) },
                actions = {
                    IconButton(onClick = onNavigateToGroupManagement) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                shape = SquircleShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .iosShadow(SquircleShape(16.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Link", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 16.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Your Groups",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(groups) { group ->
                        IosCard(
                            modifier = Modifier
                                .width(150.dp)
                                .height(120.dp)
                                .clickable { onNavigateToGroup(group.group.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(android.graphics.Color.parseColor(group.group.colorHex)))
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home, // Simplified since dynamic icons are complex to wire up
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Column {
                                        Text(
                                            text = group.group.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "${group.count} links",
                                            color = Color.White.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Recent Links",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                )
            }

            items(recentLinks.size) { index ->
                val link = recentLinks[index]
                var visible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    delay(index * 50L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(iosFade) + slideInVertically(iosSpringOffset) { it / 2 }
                ) {
                    LinkCardCompact(
                        link = link,
                        onClick = { /* TODO: Open URL */ },
                        onToggleRead = { viewModel.toggleRead(link.id, !link.isRead) },
                        onDelete = { viewModel.deleteLink(link.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (showBottomSheet) {
            GlassBottomSheet(onDismiss = { showBottomSheet = false }) {
                Text("Save a New Link", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth().clip(SquircleShape(16.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Group selector simplified
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        val isSelected = selectedGroupId == group.group.id
                        Box(
                            modifier = Modifier
                                .clip(SquircleShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedGroupId = group.group.id }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(group.group.name)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val groupId = selectedGroupId ?: groups.firstOrNull()?.group?.id
                        if (groupId != null && urlInput.isNotBlank()) {
                            addLinkViewModel.onUrlChange(urlInput)
                            addLinkViewModel.onGroupSelected(groupId)
                            addLinkViewModel.saveLink()
                            showBottomSheet = false
                            urlInput = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = SquircleShape(16.dp)
                ) {
                    Text("Save Link", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
