package com.tarantino.linkkeeper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LinkRepositoryImpl @Inject constructor(
    private val linkDao: SavedLinkDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LinkRepository {
    override fun getAllLinks(): Flow<List<SavedLink>> = linkDao.getAllLinks()

    override fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>> = linkDao.getLinksByGroup(groupId)

    override fun getUnreadLinks(): Flow<List<SavedLink>> = linkDao.getUnreadLinks()

    override fun searchLinks(query: String): Flow<List<SavedLink>> = linkDao.searchLinks(query)

    override suspend fun saveLink(link: SavedLink): Long = withContext(ioDispatcher) {
        linkDao.insertLink(link)
    }

    override suspend fun updateLink(link: SavedLink) = withContext(ioDispatcher) {
        linkDao.updateLink(link)
    }

    override suspend fun markAsRead(id: Long, isRead: Boolean) = withContext(ioDispatcher) {
        linkDao.markAsRead(id, isRead)
    }

    override suspend fun deleteLink(id: Long) = withContext(ioDispatcher) {
        linkDao.deleteLinkById(id)
    }
}
