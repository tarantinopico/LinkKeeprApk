package com.tarantino.linkkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementScreen(onBack: () -> Unit, viewModel: GroupViewModel = hiltViewModel()) {
    val groups by viewModel.groups.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#2563EB") }
    var selectedIcon by remember { mutableStateOf("Folder") }
    var isSecret by remember { mutableStateOf(false) }

    val presetColors = listOf(
        "#2563EB", // Nordic Blue
        "#DC2626", // Nordic Red
        "#059669", // Emerald
        "#D97706", // Amber
        "#7C3AED", // Violet
        "#DB2777", // Pink
        "#0D9488", // Teal
        "#64748B"  // Slate
    )
    
    val icons = listOf("Folder", "Movie", "Restaurant", "Article", "Code", "Lock")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            GlassTopAppBar(
                title = { Text("Groups", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups) { group ->
                    val iconVector = when (group.iconName) {
                        "Folder" -> Icons.Default.Home
                        "Movie" -> Icons.Default.Face
                        "Restaurant" -> Icons.Default.Favorite
                        "Article" -> Icons.Default.Email
                        "Code" -> Icons.Default.Home
                        "Lock" -> Icons.Default.Lock
                        else -> Icons.Default.Home
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .hairlineBorder(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(android.graphics.Color.parseColor(group.colorHex))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconVector, null, tint = Color.White)
                            }
                            Spacer(Modifier.size(16.dp))
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.deleteGroup(group.id) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = NordicError)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(
                        darkMode = isSystemInDarkTheme(),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    Text("Create New Group", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Group Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Color", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(presetColors) { colorHex ->
                            val color = Color(android.graphics.Color.parseColor(colorHex))
                            val isSelected = selectedColor == colorHex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color)
                                    .border(
                                        width = 3.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedColor = colorHex }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Icon", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(icons) { iconName ->
                            val vector = when (iconName) {
                                "Folder" -> Icons.Default.Home
                                "Movie" -> Icons.Default.Face
                                "Restaurant" -> Icons.Default.Favorite
                                "Article" -> Icons.Default.Email
                                "Code" -> Icons.Default.Home
                                "Lock" -> Icons.Default.Lock
                                else -> Icons.Default.Home
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedIcon = iconName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = null,
                                    tint = if (selectedIcon == iconName) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Secret Group", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = isSecret,
                            onCheckedChange = { isSecret = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.createGroup(name, selectedColor, selectedIcon, isSecret)
                                name = ""
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text("Create Group", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
