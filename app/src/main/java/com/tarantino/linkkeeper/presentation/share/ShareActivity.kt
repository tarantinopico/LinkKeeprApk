package com.tarantino.linkkeeper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {

    private val viewModel: ShareViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    viewModel.processSharedText(sharedText)
                }
            }
        }

        setContent {
            KeeprTheme {
                val uiState by viewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        GlassTopAppBar(
                            title = { Text("Save to Keepr", fontWeight = FontWeight.Bold) }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.safeDrawing)
                                .padding(16.dp)
                        ) {
                            when (val state = uiState) {
                                is ShareUiState.Idle -> {}
                                is ShareUiState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Extracting link...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                        )
                                    }
                                }
                                is ShareUiState.Success -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        if (state.metadata.imageUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = state.metadata.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(16f / 9f)
                                                    .clip(RoundedCornerShape(28.dp))
                                                    .hairlineBorder(RoundedCornerShape(28.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.height(24.dp))
                                        }

                                        Text(
                                            text = state.metadata.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = state.metadata.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Text(
                                            text = "Select Group",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            contentPadding = PaddingValues(end = 16.dp)
                                        ) {
                                            items(state.groups) { groupWithCount ->
                                                val isSelected = state.selectedGroupId == groupWithCount.group.id
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

                                        Spacer(modifier = Modifier.height(32.dp))

                                        OutlinedTextField(
                                            value = state.userNote,
                                            onValueChange = { viewModel.onUserNoteChange(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Add a note or custom title...") },
                                            shape = RoundedCornerShape(16.dp),
                                            maxLines = 3,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(48.dp))

                                        Button(
                                            onClick = { viewModel.saveLink() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            enabled = state.selectedGroupId != null
                                        ) {
                                            Text("Save to Keepr", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        }
                                    }
                                }
                                is ShareUiState.Saved -> {
                                    LaunchedEffect(Unit) {
                                        finish()
                                    }
                                }
                                is ShareUiState.Error -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Oops!",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = state.message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Button(
                                            onClick = { finish() },
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text("Close")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
