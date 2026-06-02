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
        _uiState.value = _uiState.value.copy(url = url, errorMessage = null)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(userNote = note)
    }

    fun onGroupSelected(groupId: Long) {
        _uiState.value = _uiState.value.copy(selectedGroupId = groupId)
    }

    fun saveLink(onSuccess: () -> Unit) {
        val state = _uiState.value
        val url = state.url.trim()
        val groupId = state.selectedGroupId

        if (url.isBlank()) {
            _uiState.value = state.copy(errorMessage = "URL cannot be empty")
            return
        }

        if (groupId == null) {
            _uiState.value = state.copy(errorMessage = "Please select a group")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                scrapeAndSaveLinkUseCase(url, groupId, state.userNote)
                _uiState.value = state.copy(isLoading = false, url = "", userNote = "")
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(isLoading = false, errorMessage = "Failed to save link")
            }
        }
    }
}
