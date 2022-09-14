package ru.cubesolutions.inapplib.view

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.AcceleraBannerType
import ru.cubesolutions.inapplib.parser.HTMLParser
import java.lang.ref.WeakReference

interface AcceleraViewDelegate {
    fun onReady(view: View, type: AcceleraBannerType)
    fun onAdded()
    fun onError(error: String)
    fun onAction(action: String)
    fun onClose()
}

class AcceleraBannerViewController {

    companion object {
        const val LOG_TAG_BANNER_VIEW = "TAG_BANNER_VIEW"
    }

    var bannerType: AcceleraBannerType = AcceleraBannerType.CENTER

    var delegate: WeakReference<AcceleraViewDelegate?>? = null

    fun create(html: String, bannerType: AcceleraBannerType) {
        this.bannerType = bannerType

//        if (scope.isActive) {
//            delegate?.get()?.onError("Already creating a banner. Wait for completion")
//            return
//        }

        HTMLParser().parse(html = html, { document ->
            Log.d(LOG_TAG_BANNER_VIEW, "document - $document")

        }, { error ->
            delegate?.get()?.onError(error)
        })
    }

    private fun createView(
        context: Context,
        element: Element,
        completion: () -> Unit
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