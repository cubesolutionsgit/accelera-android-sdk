package ru.cubesolutions.inapplib.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.utils.ViewUtils

class AcceleraImageView(
    context: Context,
    element: Element,
) : AcceleraAbstractView(
    element = element,
    view = ViewUtils.getImageView(
        context = context,
        element = element
    )
)