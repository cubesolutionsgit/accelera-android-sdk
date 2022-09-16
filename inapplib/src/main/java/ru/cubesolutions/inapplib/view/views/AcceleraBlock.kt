package ru.cubesolutions.inapplib.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.utils.ViewUtils

class AcceleraBlock(
    context: Context,
    element: Element,
) : AcceleraAbstractView(
    element = element,
    view = ViewUtils.getLinearLayout(
        context = context,
        element = element
    )
)