package com.tarantino.linkkeeper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkBottomSheet(
    onDismiss: () -> Unit,
    viewModel: AddLinkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onDismiss()
            viewModel.reset()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    darkMode = isSystemInDarkTheme(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Column {
                Text("Add New Link", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = uiState.url,
                    onValueChange = { viewModel.onUrlChange(it) },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                Text("Group", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(uiState.groups) { groupWithCount ->
                        val isSelected = uiState.selectedGroupId == groupWithCount.group.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onGroupSelected(groupWithCount.group.id) },
                            label = { Text(groupWithCount.group.name) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.hairlineBorder(RoundedCornerShape(16.dp))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = uiState.userNote,
                    onValueChange = { viewModel.onNoteChange(it) },
                    label = { Text("Add a note or custom title...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                )
                
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.saveLink() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading && uiState.selectedGroupId != null
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Link", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
