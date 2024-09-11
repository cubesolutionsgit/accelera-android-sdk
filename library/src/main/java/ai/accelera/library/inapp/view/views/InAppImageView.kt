package ai.accelera.library.inapp.view.views

import android.content.Context
import org.jsoup.nodes.Element
import ai.accelera.library.inapp.utils.ViewUtils

class InAppImageView(
    context: Context,
    element: Element,
) : InAppAbstractView(
    element = element,
    view = ViewUtils.getImageView(
        context = context,
        element = element
    )
)