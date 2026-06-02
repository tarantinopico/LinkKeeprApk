package com.tarantino.linkkeeper

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_links",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("groupId"),
        Index("timestamp")
    ]
)
data class SavedLink(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val url: String,
    
    val title: String,
    
    @ColumnInfo(defaultValue = "")
    val description: String = "",
    
    @ColumnInfo(defaultValue = "")
    val thumbnailUri: String = "",
    
    val groupId: Long,
    
    val timestamp: Long,
    
    @ColumnInfo(defaultValue = "0")
    val isRead: Boolean = false
)
