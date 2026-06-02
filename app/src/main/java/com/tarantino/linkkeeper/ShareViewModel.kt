package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val linkScraper: LinkScraper,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val scrapeAndSaveLinkUseCase: ScrapeAndSaveLinkUseCase,
    private val extractUrlUseCase: ExtractUrlUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Idle)
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    private var pendingUrl: String? = null

    fun processSharedText(text: String) {
        viewModelScope.launch {
            _uiState.value = ShareUiState.Loading
            val url = extractUrlUseCase(text)
            if (url == null) {
                _uiState.value = ShareUiState.Error("No valid URL found in the shared text.")
                return@launch
            }
            pendingUrl = url
            
            try {
                val metadata = linkScraper.scrape(url)
                val groups = getGroupsUseCase().first()
                if (groups.isEmpty()) {
                    _uiState.value = ShareUiState.Success(metadata, groups, -1L)
                } else {
                    _uiState.value = ShareUiState.Success(metadata, groups, groups.first().id)
                }
            } catch (e: Exception) {
                _uiState.value = ShareUiState.Error(e.message ?: "Failed to scrape link.")
            }
        }
    }

    fun selectGroup(groupId: Long) {
        val currentState = _uiState.value
        if (currentState is ShareUiState.Success) {
            _uiState.value = currentState.copy(selectedGroupId = groupId)
        }
    }

    fun saveLink() {
        val currentState = _uiState.value
        if (currentState is ShareUiState.Success) {
            val urlToSave = pendingUrl ?: return
            val groupIdToSave = if (currentState.selectedGroupId == -1L) null else currentState.selectedGroupId
            
            viewModelScope.launch {
                val result = scrapeAndSaveLinkUseCase(urlToSave, groupIdToSave)
                if (result.isSuccess) {
                    _uiState.value = ShareUiState.Saved
                } else {
                    _uiState.value = ShareUiState.Error(result.exceptionOrNull()?.message ?: "Failed to save link.")
                }
            }
        }
    }

    fun reset() {
        _uiState.value = ShareUiState.Idle
        pendingUrl = null
    }
}
