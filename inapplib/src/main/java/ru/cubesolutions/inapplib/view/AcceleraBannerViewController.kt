package ru.cubesolutions.inapplib.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.model.AcceleraBannerType
import ru.cubesolutions.inapplib.parser.HTMLParser
import ru.cubesolutions.inapplib.utils.LogUtils
import java.lang.ref.WeakReference
import java.util.*

class AcceleraBannerViewController {

    companion object {
        const val TAG_IN_APP_BANNER_VIEW = "IN_APP_BANNER_VIEW"
    }

    var bannerType: AcceleraBannerType = AcceleraBannerType.CENTER

    var delegate: WeakReference<AcceleraViewDelegate?>? = null

    private var parentView: LinearLayout? = null
    private var previouslyView: View? = null

    private var isLoading = false

    fun create(context: Context, html: String, bannerType: AcceleraBannerType) {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create html - $html")
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create bannerType - $bannerType")

        if (isLoading) {
            delegate?.get()?.onError("Already creating a banner. Wait for completion")
            return
        }

        this.bannerType = bannerType

        clear()

        HTMLParser().parse(html = html, { document ->
            LogUtils.info(TAG_IN_APP_BANNER_VIEW, "document - $document")

            // Флаг о том, что работа над созданием view началась
            isLoading = true

            document.allElements.forEach {
                val tagName = it.tagName()
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create tagName - $tagName")
                val size = it.allElements.size
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create allElements size - $size")
                val attribute = it.attr("href")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create attribute - $attribute")
                val fontSize = it.attr("font-size")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create fontSize - $fontSize")
                val width = it.attr("width")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create width - $width")
                val level = it.attr("level")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create level - $level")
                val color = it.attr("color")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create color - $color")
                val src = it.attr("src")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create src - $src")
                val margin = it.attr("margin")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create src - $margin")
                val backgroundColor = it.attr("background-color")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create backgroundColor - $backgroundColor")

                createView(
                    context = context,
                    element = it,
                )
            }
            if (parentView != null) {
                delegate?.get()?.onReady(view = parentView!!, type = bannerType)
            } else {
                delegate?.get()?.onError("View was not created properly")
            }

            // Работа над созданием view закончилась
            isLoading = false
        }, { error ->
            delegate?.get()?.onError(error)
        })
    }

    fun clear() {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "clear")

        // Очистка данных в контроллере
        this.parentView = null
        this.previouslyView = null
        this.bannerType = AcceleraBannerType.CENTER
    }

    private fun createView(
        context: Context,
        element: Element,
    ) {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "createView element - $element")

        // Провеярем tag распарсенного элемента HTML
        when (element.tagName()) {
            "re-body" -> {
                // Создали view из тега re-body - это верхнеуровневый view
                val linLayout = LinearLayout(context)
                val backgroundColor = element.attr("background-color")
                val color = Color.parseColor(backgroundColor)
                linLayout.setBackgroundColor(color)
                linLayout.layoutParams = LinearLayout.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
                val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
                linLayout.tag = uuidFromElement
                // Если родительского view не существует, то считаем, что созданный view родительский
                if (parentView == null) {
                    parentView = linLayout
                } else {
                    // Если внутри элемента, находятся еще элементы, то считаем, что временно
                    // сохраняем view в контроллер, а сам созданный элемент добавляем
                    if (element.allElements.size > 1 && previouslyView == null) {
                        previouslyView = linLayout
                        parentView?.let {
                            it.addView(previouslyView)
                        }
                    } else if (previouslyView != null && previouslyView is LinearLayout) {
                        (previouslyView as? LinearLayout)?.let {
                            it.addView(linLayout)
                        }
                    }
                }
            }
            "re-main" -> {

            }
            "re-block" -> {

            }
            "re-heading" -> {

            }
            "re-text" -> {

            }
            "re-image" -> {

            }
            "re-button" -> {

            }
        }
    }
}