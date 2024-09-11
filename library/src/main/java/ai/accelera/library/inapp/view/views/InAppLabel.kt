package ai.accelera.library.inapp.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ai.accelera.library.inapp.utils.ViewUtils

class InAppLabel(
    context: Context,
    element: Element,
) : InAppAbstractView(
    element = element,
    view = ViewUtils.getTextView(
        context = context,
        element = element
    )
)