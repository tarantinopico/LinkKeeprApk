package com.tarantino.linkkeeper

import kotlinx.coroutines.flow.Flow

interface LinkRepository {
    fun getAllLinks(): Flow<List<SavedLink>>
    fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>>
    fun getUnreadLinks(): Flow<List<SavedLink>>
    fun searchLinks(query: String): Flow<List<SavedLink>>
    suspend fun saveLink(link: SavedLink): Long
    suspend fun updateLink(link: SavedLink)
    suspend fun markAsRead(id: Long, isRead: Boolean)
    suspend fun deleteLink(id: Long)
}
