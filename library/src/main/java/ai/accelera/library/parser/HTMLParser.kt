package ai.accelera.library.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ai.accelera.library.utils.LogUtils

class HTMLParser {

    companion object {
        const val LOG_TAG_HTML_PARSER = "IN_APP_HTML_PARSER"
    }

    private val tagsToReplace = listOf("br", "b", "/b", "u", "/u", "i", "/i", "strong", "/strong")

    fun parse(
        html: String,
        completion: (Document) -> Unit,
        onError: (String) -> Unit,
    ) {
        LogUtils.info(LOG_TAG_HTML_PARSER, "parse html - $html")

        try {
            var newHtml = html

            // TODO: use regexp
            tagsToReplace.forEach { tag ->
                newHtml = newHtml.replace("<\\($tag)>", "!@#\\($tag)!@#")
            }

            LogUtils.info(LOG_TAG_HTML_PARSER, "parse newHtml - $newHtml")

            // TODO: Парсинг html через JSOUP. переделать на кастомное решение
            val parseData = Jsoup.parse(newHtml)

            // Отправка ответа в колбэк
            completion.invoke(parseData)
        } catch (exception: Exception) {
            onError.invoke(exception.message.toString())
        }
    }
}