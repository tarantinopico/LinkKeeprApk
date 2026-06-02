package com.tarantino.linkkeeper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {

    override suspend fun insertGroup(group: Group): Long = withContext(ioDispatcher) {
        groupDao.insertGroup(group)
    }

    override suspend fun updateGroup(group: Group) = withContext(ioDispatcher) {
        groupDao.updateGroup(group)
    }

    override suspend fun deleteGroup(group: Group) = withContext(ioDispatcher) {
        groupDao.deleteGroup(group)
    }

    override suspend fun deleteGroupById(id: Long) = withContext(ioDispatcher) {
        groupDao.deleteGroupById(id)
    }

    override fun getAllGroups(): Flow<List<Group>> {
        return groupDao.getAllGroups()
    }

    override suspend fun getGroupById(id: Long): Group? = withContext(ioDispatcher) {
        groupDao.getGroupById(id)
    }

    override suspend fun getGroupCount(): Int = withContext(ioDispatcher) {
        groupDao.getGroupCount()
    }
}
