package com.tarantino.linkkeeper

import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException
import org.jsoup.HttpStatusException

class LinkScraper @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun scrape(url: String): ScrapedMetadata = withContext(ioDispatcher) {
        val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }

        val domain = runCatching { Uri.parse(validUrl).host }.getOrNull() ?: "Unknown Link"

        try {
            val document = Jsoup.connect(validUrl)
                .userAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
                .timeout(10000)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .get()

            val ogTitle = document.select("meta[property=og:title]").attr("content")
            val htmlTitle = document.title()
            val title = ogTitle.takeIf { it.isNotBlank() }
                ?: htmlTitle.takeIf { it.isNotBlank() }
                ?: domain

            val ogDescription = document.select("meta[property=og:description]").attr("content")
            val metaDescription = document.select("meta[name=description]").attr("content")
            val description = ogDescription.takeIf { it.isNotBlank() }
                ?: metaDescription.takeIf { it.isNotBlank() }
                ?: ""

            val ogImage = document.select("meta[property=og:image]").attr("content")
            val imageUrl = ogImage.takeIf { it.isNotBlank() } ?: ""

            ScrapedMetadata(title, description, imageUrl)
        } catch (e: Exception) {
            ScrapedMetadata(title = domain, description = "", imageUrl = "")
        }
    }
}
