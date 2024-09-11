package ai.accelera.library.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ai.accelera.library.utils.ViewUtils

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