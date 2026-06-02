package com.tarantino.linkkeeper

import javax.inject.Inject

class ScrapeAndSaveLinkUseCase @Inject constructor(
    private val linkRepository: LinkRepository,
    private val extractUrlUseCase: ExtractUrlUseCase,
    private val linkScraper: LinkScraper
) {
    suspend operator fun invoke(rawText: String, groupId: Long, userNote: String = "") {
        val url = extractUrlUseCase(rawText)
        if (url != null) {
            val scrapedData = linkScraper.scrape(url)
            val savedLink = SavedLink(
                url = url,
                title = scrapedData.title.ifBlank { url },
                description = scrapedData.description,
                thumbnailUri = scrapedData.imageUrl,
                groupId = groupId,
                userNote = userNote
            )
            linkRepository.saveLink(savedLink)
        }
    }
}
