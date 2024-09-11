package ai.accelera.library.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ai.accelera.library.utils.ViewUtils

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