package com.tarantino.linkkeeper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLinkDao {
    @Query("SELECT * FROM saved_links ORDER BY timestamp DESC")
    fun getAllLinks(): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE group_id = :groupId ORDER BY timestamp DESC")
    fun getLinksByGroup(groupId: Long): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE is_read = 0 ORDER BY timestamp DESC")
    fun getUnreadLinks(): Flow<List<SavedLink>>

    @Query("SELECT * FROM saved_links WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' OR user_note LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLinks(query: String): Flow<List<SavedLink>>

    @Query("UPDATE saved_links SET is_read = :isRead WHERE id = :linkId")
    suspend fun markAsRead(linkId: Long, isRead: Boolean)

    @Query("DELETE FROM saved_links WHERE id = :linkId")
    suspend fun deleteLinkById(linkId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: SavedLink): Long

    @Update
    suspend fun updateLink(link: SavedLink)
}
