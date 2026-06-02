package com.tarantino.linkkeeper

data class AddLinkUiState(
    val url: String = "",
    val userNote: String = "",
    val selectedGroupId: Long? = null,
    val groups: List<GroupWithCount> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
