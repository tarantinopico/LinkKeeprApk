package com.tarantino.linkkeeper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        
        val url = intent?.getStringExtra(Intent.EXTRA_TEXT)?.let { extractUrl(it) }

        if (url == null) {
            finish()
            return
        }

        setContent {
            val darkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            KeeprTheme(darkTheme = darkTheme) {
                ShareScreen(
                    url = url,
                    onClose = { finish() }
                )
            }
        }
    }

    private fun extractUrl(text: String): String? {
        val urlRegex = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))".toRegex()
        return urlRegex.find(text)?.value
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    url: String,
    onClose: () -> Unit,
    viewModel: ShareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(url) {
        viewModel.handleIntent(url)
    }
    
    if (uiState is ShareUiState.Saved) {
        LaunchedEffect(Unit) {
            onClose()
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                GlassTopAppBar(
                    title = { Text("Save Link", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (val state = uiState) {
                    is ShareUiState.Initial, is ShareUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Extracting details...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    is ShareUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth().hairlineBorder(RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 0.dp
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    if (state.scrapedMetadata.imageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = state.scrapedMetadata.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(16f / 9f)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                    
                                    Text(
                                        text = state.scrapedMetadata.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.scrapedMetadata.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Column {
                                Text("Choose Group", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(state.groups) { group ->
                                        FilterChip(
                                            selected = state.selectedGroupId == group.group.id,
                                            onClick = { viewModel.onGroupSelected(group.group.id) },
                                            label = { Text(group.group.name) },
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = state.userNote,
                                onValueChange = { viewModel.onUserNoteChange(it) },
                                label = { Text("Add a note or custom title...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                maxLines = 3
                            )

                            Button(
                                onClick = { viewModel.saveLink() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = state.selectedGroupId != null
                            ) {
                                Text("Save Link", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                            }
                        }
                    }
                    is ShareUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.handleIntent(url) }, shape = RoundedCornerShape(16.dp)) {
                                Text("Try Again")
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
