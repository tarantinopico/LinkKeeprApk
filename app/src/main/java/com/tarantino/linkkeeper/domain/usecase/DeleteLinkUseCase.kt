package com.tarantino.linkkeeper

import javax.inject.Inject

class DeleteLinkUseCase @Inject constructor(
    private val linkRepository: LinkRepository
) {
    suspend operator fun invoke(linkId: Long) = linkRepository.deleteLink(linkId)
}
