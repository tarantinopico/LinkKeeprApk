package com.tarantino.linkkeeper

import javax.inject.Inject

class ToggleReadUseCase @Inject constructor(
    private val linkRepository: LinkRepository
) {
    suspend operator fun invoke(linkId: Long, isRead: Boolean) = linkRepository.markAsRead(linkId, isRead)
}
