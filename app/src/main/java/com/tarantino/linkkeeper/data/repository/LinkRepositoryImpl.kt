package com.tarantino.linkkeeper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LinkRepositoryImpl @Inject constructor(
    private val savedLinkDao: SavedLinkDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LinkRepository {

    override suspend fun saveLink(link: SavedLink): Long = withContext(ioDispatcher) {
        savedLinkDao.insertLink(link)
    }

    override suspend fun updateLink(link: SavedLink) = withContext(ioDispatcher) {
        savedLinkDao.updateLink(link)
    }

    override suspend fun deleteLink(link: SavedLink) = withContext(ioDispatcher) {
        savedLinkDao.deleteLink(link)
    }

    override suspend fun deleteLinkById(id: Long) = withContext(ioDispatcher) {
        savedLinkDao.deleteLinkById(id)
    }

    override suspend fun markAsRead(id: Long, isRead: Boolean) = withContext(ioDispatcher) {
        savedLinkDao.markAsRead(id, isRead)
    }

    override fun getAllLinks(): Flow<List<SavedLink>> {
        return savedLinkDao.getAllLinks()
    }

    override fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>> {
        return savedLinkDao.getLinksByGroup(groupId)
    }

    override fun getUnreadLinks(): Flow<List<SavedLink>> {
        return savedLinkDao.getUnreadLinks()
    }

    override fun searchLinks(query: String): Flow<List<SavedLink>> {
        return savedLinkDao.searchLinks(query)
    }

    override fun getLinkCountByGroup(groupId: Long): Flow<Int> {
        return savedLinkDao.getLinkCountByGroup(groupId)
    }
}
