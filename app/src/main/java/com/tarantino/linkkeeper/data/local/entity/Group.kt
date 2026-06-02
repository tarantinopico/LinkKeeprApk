package com.tarantino.linkkeeper

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    @ColumnInfo(defaultValue = "#6750A4")
    val colorHex: String = "#6750A4",
    
    @ColumnInfo(defaultValue = "Folder")
    val iconName: String = "Folder",
    
    @ColumnInfo(defaultValue = "0")
    val isSecret: Boolean = false
)
