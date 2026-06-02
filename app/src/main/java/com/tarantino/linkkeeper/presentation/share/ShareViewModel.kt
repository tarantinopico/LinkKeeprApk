package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val extractUrlUseCase: ExtractUrlUseCase,
    private val linkScraper: LinkScraper,
    private val linkRepository: LinkRepository,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var currentUrl: String = ""

    fun processSharedText(text: String) {
        viewModelScope.launch {
            _uiState.value = ShareUiState.Loading
            val url = extractUrlUseCase(text)
            if (url == null) {
                _uiState.value = ShareUiState.Error("No valid URL found in the shared text.")
                return@launch
            }

            currentUrl = url
            val groups = getGroupsUseCase().first()
            val links = linkRepository.getAllLinks().first()
            val groupsWithCount = groups.map { group ->
                GroupWithCount(group, links.count { it.groupId == group.id })
            }
            val scrapedData = linkScraper.scrape(url)

            _uiState.value = ShareUiState.Success(
                metadata = scrapedData,
                groups = groupsWithCount,
                selectedGroupId = groups.firstOrNull()?.id,
                userNote = ""
            )
        }
    }

    fun onGroupSelected(groupId: Long) {
        val currentState = _uiState.value
        if (currentState is ShareUiState.Success) {
            _uiState.value = currentState.copy(selectedGroupId = groupId)
        }
    }
    
    fun onUserNoteChange(note: String) {
        val currentState = _uiState.value
        if (currentState is ShareUiState.Success) {
            _uiState.value = currentState.copy(userNote = note)
        }
    }

    fun saveLink() {
        val currentState = _uiState.value
        if (currentState is ShareUiState.Success) {
            viewModelScope.launch {
                val groupId = currentState.selectedGroupId ?: return@launch
                val savedLink = SavedLink(
                    url = currentUrl,
                    title = currentState.metadata.title.ifBlank { currentUrl },
                    description = currentState.metadata.description,
                    thumbnailUri = currentState.metadata.imageUrl,
                    groupId = groupId,
                    userNote = currentState.userNote
                )
                linkRepository.saveLink(savedLink)
                _uiState.value = ShareUiState.Saved
            }
        }
    }
}
