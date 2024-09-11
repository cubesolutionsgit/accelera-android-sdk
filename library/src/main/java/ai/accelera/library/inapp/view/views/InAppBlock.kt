package ai.accelera.library.inapp.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ai.accelera.library.inapp.utils.ViewUtils

class InAppBlock(
    context: Context,
    element: Element,
) : InAppAbstractView(
    element = element,
    view = ViewUtils.getLinearLayout(
        context = context,
        element = element
    )
)