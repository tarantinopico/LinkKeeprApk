package com.tarantino.linkkeeper

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Group::class, SavedLink::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun savedLinkDao(): SavedLinkDao
}
