package com.tarantino.linkkeeper

sealed interface ShareUiState {
    data object Idle : ShareUiState
    data object Loading : ShareUiState
    data class Success(val metadata: ScrapedMetadata, val groups: List<Group>, val selectedGroupId: Long) : ShareUiState
    data object Saved : ShareUiState
    data class Error(val message: String) : ShareUiState
}
