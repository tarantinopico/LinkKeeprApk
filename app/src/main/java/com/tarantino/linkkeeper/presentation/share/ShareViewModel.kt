package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val scrapeAndSaveLinkUseCase: ScrapeAndSaveLinkUseCase,
    private val linkRepository: LinkRepository,
    private val linkScraper: LinkScraper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private var currentUrl: String? = null

    fun handleIntent(url: String) {
        if (_uiState.value !is ShareUiState.Initial) return
        
        viewModelScope.launch {
            _uiState.value = ShareUiState.Loading

            currentUrl = url
            val groups = getGroupsUseCase().first()
            val links = linkRepository.getAllLinks().first()
            val groupsWithCount = groups.map { group ->
                GroupWithCount(group, links.count { it.groupId == group.id })
            }
            val scrapedData = linkScraper.scrape(url)

            _uiState.value = ShareUiState.Success(
                scrapedMetadata = scrapedData,
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
            val groupId = currentState.selectedGroupId ?: return
            val url = currentUrl ?: return
            
            viewModelScope.launch {
                scrapeAndSaveLinkUseCase(url, groupId, currentState.userNote)
                _uiState.value = ShareUiState.Saved
            }
        }
    }
}
