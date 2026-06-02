package com.tarantino.linkkeeper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val groupRepository: GroupRepository,
    private val linkRepository: LinkRepository
) : ViewModel() {
    
    val groups: StateFlow<List<Group>> = groupRepository.getAllGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGroup(name: String, colorHex: String, iconName: String, isSecret: Boolean) {
        viewModelScope.launch {
            createGroupUseCase(name, colorHex, iconName, isSecret)
        }
    }

    fun deleteGroup(id: Long) {
        viewModelScope.launch {
            groupRepository.deleteGroupById(id)
        }
    }
}
