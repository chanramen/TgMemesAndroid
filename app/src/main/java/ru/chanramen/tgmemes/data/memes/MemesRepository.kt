package ru.chanramen.tgmemes.data.memes

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import ru.chanramen.tgmemes.domain.ChannelName

private const val PHOTO_CLASS = "tgme_widget_message_photo_wrap"
private const val AUTHOR_CLASS = "tgme_widget_message_from_author"
private const val VIEWS_CLASS = "tgme_widget_message_views"

class MemesRepository(private val client: HttpClient) {

    private val urlRegex by lazy {
        "background-image:url\\('(.+)'\\)".toRegex()
    }

    suspend fun loadLastMeme(name: ChannelName): MemeInfoResult {
        val response = client.get(name.createPreviewUrl())
        val body = response.bodyAsText()
        return withContext(Dispatchers.Default) {
            val element = Jsoup.parse(body)
                .getElementsByClass(PHOTO_CLASS)
                .last() ?: return@withContext MemeInfoResult.EmptyData
            val url = element
                .attr("style")
                .takeIf(String::isNotBlank)?.let {
                    urlRegex.find(it)?.groups?.get(1)?.value
                } ?: return@withContext MemeInfoResult.EmptyData
            val author = element.parent()?.getElementsByClass(AUTHOR_CLASS)?.first()?.text()
            val views = element.parent()?.getElementsByClass(VIEWS_CLASS)?.first()?.text()?.toIntOrNull()
            val postUrl = element.attr("href")
            val image = client.get(url).readBytes()
            MemeInfoResult.Success(
                MemeInfo(
                    image = image,
                    author = author,
                    views = views,
                    postUrl = postUrl,
                )
            )
        }
    }
}