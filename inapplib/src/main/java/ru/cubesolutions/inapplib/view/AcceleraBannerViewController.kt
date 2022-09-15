package ru.cubesolutions.inapplib.view

import android.content.Context
import android.view.View
import android.widget.*
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.model.AcceleraBannerType
import ru.cubesolutions.inapplib.parser.HTMLParser
import ru.cubesolutions.inapplib.utils.LogUtils
import java.lang.ref.WeakReference

class AcceleraBannerViewController {

    companion object {
        const val TAG_IN_APP_BANNER_VIEW = "IN_APP_BANNER_VIEW"
    }

    var bannerType: AcceleraBannerType = AcceleraBannerType.CENTER

    var delegate: WeakReference<AcceleraViewDelegate?>? = null

    fun create(context: Context, html: String, bannerType: AcceleraBannerType) {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create html - $html")
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create bannerType - $bannerType")

        this.bannerType = bannerType

//        if (scope.isActive) {
//            delegate?.get()?.onError("Already creating a banner. Wait for completion")
//            return
//        }

        HTMLParser().parse(html = html, { document ->
            LogUtils.info(TAG_IN_APP_BANNER_VIEW, "document - $document")
            document.allElements.forEach {
                val size = it.allElements.size
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create forEach - $it")
                LogUtils.info(TAG_IN_APP_BANNER_VIEW, "create size - $size")
            }
        }, { error ->
            delegate?.get()?.onError(error)
        })
    }

    fun clear() {

    }

    private fun createView(
        context: Context,
        element: Element,
    ): View? {

        var view: View? = null

        when (element.tagName()) {
            "re-body" -> {
                return FrameLayout(context)
            }
            "re-main" -> {
                return FrameLayout(context)
            }
            "re-block" -> {
                return FrameLayout(context)
            }
            "re-heading" -> {
                return TextView(context).apply {
                    this.text = "re-heading"
                }
            }
            "re-text" -> {
                return TextView(context).apply {
                    this.text = "re-text"
                }
            }
            "re-image" -> {
                return ImageView(context)
            }
            "re-button" -> {
                return Button(context).apply {
                    this.text = "re-button"
                }
            }
        }

        return view
    }
}