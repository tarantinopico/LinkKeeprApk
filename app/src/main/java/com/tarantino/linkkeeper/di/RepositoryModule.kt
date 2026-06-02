package com.tarantino.linkkeeper

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    abstract fun bindLinkRepository(
        linkRepositoryImpl: LinkRepositoryImpl
    ): LinkRepository
}
