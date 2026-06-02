package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLinkViewModel @Inject constructor(
    private val scrapeAndSaveLinkUseCase: ScrapeAndSaveLinkUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddLinkUiState())
    val uiState: StateFlow<AddLinkUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGroupsUseCase().collect { groups ->
                _uiState.update { it.copy(groups = groups) }
            }
        }
    }

    fun onUrlChange(url: String) {
        _uiState.update { it.copy(url = url, error = null) }
    }

    fun onGroupSelected(id: Long?) {
        _uiState.update { it.copy(selectedGroupId = id) }
    }

    fun saveLink() {
        val currentState = _uiState.value
        if (currentState.url.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = scrapeAndSaveLinkUseCase(currentState.url, currentState.selectedGroupId)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Failed to save link") }
            }
        }
    }
}
