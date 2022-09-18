package ru.cubesolutions.inapplib.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.model.AcceleraBannerType
import ru.cubesolutions.inapplib.parser.HTMLParser
import ru.cubesolutions.inapplib.utils.LogUtils
import ru.cubesolutions.inapplib.utils.ViewUtils
import ru.cubesolutions.inapplib.view.views.*
import java.lang.ref.WeakReference

class AcceleraBannerViewController {

    companion object {
        const val TAG_IN_APP_BANNER_VIEW = "IN_APP_BANNER_VIEW"
    }

    var bannerType: AcceleraBannerType = AcceleraBannerType.CENTER

    var delegate: WeakReference<AcceleraViewDelegate?>? = null

    private var view: View? = null

    private var parsingParents = mutableListOf<AcceleraAbstractView>()
    private var topView: AcceleraAbstractView? = null

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
        this.bannerType = AcceleraBannerType.CENTER
    }

    private fun parseElement(
        context: Context,
        element: Element,
    ): AcceleraAbstractView? {
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

    private fun renderView(view: AcceleraAbstractView?, parent: AcceleraAbstractView?) {
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
    ): AcceleraAbstractView? {
        LogUtils.info(TAG_IN_APP_BANNER_VIEW, "createView element - $element")

        // Провеярем tag распарсенного элемента HTML
        when (element.tagName()) {
            "re-body" -> {
                return AcceleraBlock(
                    context = context,
                    element = element,
                )
            }
            "re-main" -> {
                return AcceleraBlock(
                    context = context,
                    element = element,
                )
            }
            "re-block" -> {
                return AcceleraBlock(
                    context = context,
                    element = element,
                )
            }
            "re-heading" -> {
                return AcceleraLabel(
                    context = context,
                    element = element,
                )
            }
            "re-text" -> {
                return AcceleraLabel(
                    context = context,
                    element = element,
                )
            }
            "re-image" -> {
                return AcceleraImageView(
                    context = context,
                    element = element,
                )
            }
            "re-button" -> {
                return AcceleraButton(
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