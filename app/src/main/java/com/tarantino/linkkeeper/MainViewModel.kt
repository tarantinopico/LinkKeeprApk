package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val linkRepository: LinkRepository,
    private val groupRepository: GroupRepository,
    private val deleteLinkUseCase: DeleteLinkUseCase,
    private val toggleReadUseCase: ToggleReadUseCase
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val groupsWithCount: StateFlow<List<GroupWithCount>> = groupRepository.getAllGroups()
        .flatMapLatest { groups ->
            val countFlows = groups.map { group ->
                linkRepository.getLinkCountByGroup(group.id).map { count -> GroupWithCount(group, count) }
            }
            if (countFlows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(countFlows) { it.toList() }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentLinks: StateFlow<List<SavedLink>> = linkRepository.getAllLinks()
        .map { it.take(10) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>> = linkRepository.getLinksByGroup(groupId)
}
