package ai.accelera.library.inapp.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.jsoup.nodes.Element
import ai.accelera.library.inapp.model.InAppBannerType
import ai.accelera.library.inapp.parser.HTMLParser
import ai.accelera.library.utils.LogUtils
import ai.accelera.library.inapp.utils.ViewUtils
import ai.accelera.library.inapp.view.views.InAppAbstractView
import ai.accelera.library.inapp.view.views.InAppBlock
import ai.accelera.library.inapp.view.views.InAppButton
import ai.accelera.library.inapp.view.views.InAppImageView
import ai.accelera.library.inapp.view.views.InAppLabel
import java.lang.ref.WeakReference

class InAppBannerViewController {

    companion object {
        const val TAG_IN_APP_BANNER_VIEW = "IN_APP_BANNER_VIEW"
    }

    var bannerType: InAppBannerType = InAppBannerType.CENTER

    var delegate: WeakReference<InAppViewDelegate?>? = null

    private var view: View? = null

    private var parsingParents = mutableListOf<InAppAbstractView>()
    private var topView: InAppAbstractView? = null

    private var isLoading = false

    fun create(context: Context, html: String, bannerType: InAppBannerType) {
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

            parseElement(
                context = context,
                element = document,
            )

            render(
                context = context
            )

            if (view != null) {
                delegate?.get()?.onReady(view = view!!, type = bannerType)
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
        this.view = null
        this.parsingParents.clear()
        this.bannerType = InAppBannerType.CENTER
    }

    private fun parseElement(
        context: Context,
        element: Element,
    ): InAppAbstractView? {
        val view = createView(context, element)

        view?.let {
            parsingParents.lastOrNull()?.descendents?.add(it)
            parsingParents.add(it)
        }

        element.children().forEach { child ->
            parseElement(
                context = context,
                element = child,
            )
        }

        if (view != null) {
            // assume that we can only have one top element (re-body)
            if (parsingParents.size == 1) {
                topView = parsingParents.firstOrNull()
            }
            parsingParents.removeLastOrNull()
        }

        return view
    }

    private fun render(context: Context) {
        if (topView == null) {
            return
        }

        val superview = ViewUtils.getSuperLinearLayout(context)
        this.view = superview

        //TODO Added listener
//        superview.delegate = self

        topView?.view?.let {
            superview.addView(it)
        }

        topView?.descendents?.forEach { child ->
            renderView(view = child, parent = topView)
        }
    }

    private fun renderView(view: InAppAbstractView?, parent: InAppAbstractView?) {
        val parentView = parent?.view
        if (parentView is ViewGroup) {
            parentView.addView(view?.view)
        }

        view?.descendents?.forEach { child ->
            renderView(view = child, parent = view)
        }
    }

    private fun createView(
        context: Context,
        element: Element,
    ): InAppAbstractView? {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "createView element - $element")

        // Провеярем tag распарсенного элемента HTML
        when (element.tagName()) {
            "re-body" -> {
                return InAppBlock(
                    context = context,
                    element = element,
                )
            }
            "re-main" -> {
                return InAppBlock(
                    context = context,
                    element = element,
                )
            }
            "re-block" -> {
                return InAppBlock(
                    context = context,
                    element = element,
                )
            }
            "re-heading" -> {
                return InAppLabel(
                    context = context,
                    element = element,
                )
            }
            "re-text" -> {
                return InAppLabel(
                    context = context,
                    element = element,
                )
            }
            "re-image" -> {
                return InAppImageView(
                    context = context,
                    element = element,
                )
            }
            "re-button" -> {
                return InAppButton(
                    context = context,
                    element = element,
                    action = {
                        delegate?.get()?.onAction(it)
                    }
                )
            }
            else -> {
                return null
            }
        }
    }
}