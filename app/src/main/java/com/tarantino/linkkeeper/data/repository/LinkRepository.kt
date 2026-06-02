package com.tarantino.linkkeeper

import kotlinx.coroutines.flow.Flow

interface LinkRepository {
    suspend fun saveLink(link: SavedLink): Long
    suspend fun updateLink(link: SavedLink)
    suspend fun deleteLink(link: SavedLink)
    suspend fun deleteLinkById(id: Long)
    suspend fun markAsRead(id: Long, isRead: Boolean)
    fun getAllLinks(): Flow<List<SavedLink>>
    fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>>
    fun getUnreadLinks(): Flow<List<SavedLink>>
    fun searchLinks(query: String): Flow<List<SavedLink>>
    fun getLinkCountByGroup(groupId: Long): Flow<Int>
}
