package com.tarantino.linkkeeper

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLinkDao {
    @Insert
    suspend fun insertLink(link: SavedLink): Long

    @Update
    suspend fun updateLink(link: SavedLink)

    @Delete
    suspend fun deleteLink(link: SavedLink)

    @Query("SELECT * FROM saved_links ORDER BY timestamp DESC")
    fun getAllLinks(): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadLinks(): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLinks(query: String): Flow<List<SavedLink>>

    @Query("UPDATE saved_links SET isRead = :isRead WHERE id = :id")
    suspend fun markAsRead(id: Long, isRead: Boolean)

    @Query("DELETE FROM saved_links WHERE id = :id")
    suspend fun deleteLinkById(id: Long)

    @Query("SELECT COUNT(*) FROM saved_links WHERE groupId = :groupId")
    fun getLinkCountByGroup(groupId: Long): Flow<Int>
}
