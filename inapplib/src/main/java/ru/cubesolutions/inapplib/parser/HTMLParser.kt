package ru.cubesolutions.inapplib.parser

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HTMLParser {

    companion object {
        const val LOG_TAG_HTML_PARSER = "TAG_HTML_PARSER"
    }

    private val tagsToReplace = listOf("br", "b", "/b", "u", "/u", "i", "/i", "strong", "/strong")

    fun parse(
        html: String,
        completion: (Document) -> Unit,
        onError: (String) -> Unit,
    ) {
        Log.d(LOG_TAG_HTML_PARSER, html)
        var newHtml = html

        // TODO: use regexp
        tagsToReplace.forEach { tag ->
            newHtml = newHtml.replace("<\\($tag)>", "!@#\\($tag)!@#")
        }

        Log.d(LOG_TAG_HTML_PARSER, newHtml)

        val parseData = Jsoup.parse(newHtml)

        completion.invoke(parseData)
    }
}