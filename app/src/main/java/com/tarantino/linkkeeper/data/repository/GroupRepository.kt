package com.tarantino.linkkeeper

import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun insertGroup(group: Group): Long
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun deleteGroupById(id: Long)
    fun getAllGroups(): Flow<List<Group>>
    suspend fun getGroupById(id: Long): Group?
    suspend fun getGroupCount(): Int
}
