package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val linkRepository: LinkRepository,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val toggleReadUseCase: ToggleReadUseCase,
    private val deleteLinkUseCase: DeleteLinkUseCase,
    private val openLinkUseCase: OpenLinkUseCase,
    private val copyLinkUseCase: CopyLinkUseCase
) : ViewModel() {

    val groupsWithCount: StateFlow<List<GroupWithCount>> = kotlinx.coroutines.flow.combine(
        getGroupsUseCase(),
        linkRepository.getAllLinks()
    ) { groups, links ->
        groups.map { group ->
            GroupWithCount(group, links.count { it.groupId == group.id })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLinks: StateFlow<List<SavedLink>> = linkRepository.getAllLinks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    fun deleteLink(id: Long) {
        viewModelScope.launch {
            deleteLinkUseCase(id)
        }
    }

    fun toggleRead(id: Long, isRead: Boolean) {
        viewModelScope.launch {
            toggleReadUseCase(id, isRead)
        }
    }

    fun openLink(url: String) {
        openLinkUseCase(url)
    }

    fun copyLink(url: String) {
        if (copyLinkUseCase(url)) {
            viewModelScope.launch {
                _snackbarEvent.emit("Link copied to clipboard")
            }
        }
    }

    fun getLinksByGroup(groupId: Long) = linkRepository.getLinksByGroup(groupId)
}
