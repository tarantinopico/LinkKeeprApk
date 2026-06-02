package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLinkViewModel @Inject constructor(
    private val scrapeAndSaveLinkUseCase: ScrapeAndSaveLinkUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val linkRepository: LinkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLinkUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getGroupsUseCase(),
                linkRepository.getAllLinks()
            ) { groups, links ->
                groups.map { group ->
                    GroupWithCount(group, links.count { it.groupId == group.id })
                }
            }.collectLatest { groups ->
                _uiState.value = _uiState.value.copy(
                    groups = groups,
                    selectedGroupId = _uiState.value.selectedGroupId ?: groups.firstOrNull()?.group?.id
                )
            }
        }
    }

    fun onUrlChange(url: String) {
        _uiState.value = _uiState.value.copy(url = url, error = null)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(userNote = note)
    }

    fun onGroupSelected(groupId: Long) {
        _uiState.value = _uiState.value.copy(selectedGroupId = groupId)
    }

    fun saveLink() {
        val currentState = _uiState.value
        if (currentState.url.isBlank()) {
            _uiState.value = currentState.copy(error = "URL cannot be empty")
            return
        }
        val groupId = currentState.selectedGroupId ?: return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)
            try {
                scrapeAndSaveLinkUseCase(currentState.url, groupId, currentState.userNote)
                _uiState.value = currentState.copy(isLoading = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = currentState.copy(isLoading = false, error = "Failed to save link")
            }
        }
    }
    
    fun reset() {
        _uiState.value = AddLinkUiState(
            groups = _uiState.value.groups,
            selectedGroupId = _uiState.value.groups.firstOrNull()?.group?.id
        )
    }
}
