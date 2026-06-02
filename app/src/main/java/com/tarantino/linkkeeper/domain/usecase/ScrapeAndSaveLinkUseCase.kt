package com.tarantino.linkkeeper

import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScrapeAndSaveLinkUseCase @Inject constructor(
    private val linkScraper: LinkScraper,
    private val linkRepository: LinkRepository,
    private val groupRepository: GroupRepository,
    private val extractUrlUseCase: ExtractUrlUseCase
) {
    suspend operator fun invoke(rawText: String, groupId: Long? = null): Result<Long> {
        return try {
            val url = extractUrlUseCase(rawText) ?: return Result.failure(IllegalArgumentException("No valid URL found"))
            
            val metadata = linkScraper.scrape(url)
            
            val targetGroupId = if (groupId == null) {
                val groups = groupRepository.getAllGroups().first()
                if (groups.isEmpty()) {
                    groupRepository.insertGroup(
                        Group(
                            name = "Uncategorized",
                            colorHex = "#6750A4",
                            iconName = "Folder",
                            isSecret = false
                        )
                    )
                } else {
                    groups.first().id
                }
            } else {
                groupId
            }

            val savedLink = SavedLink(
                url = url,
                title = metadata.title,
                description = metadata.description,
                thumbnailUri = metadata.imageUrl,
                groupId = targetGroupId,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            val linkId = linkRepository.saveLink(savedLink)
            Result.success(linkId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
