package ai.accelera.library.view.views

import android.view.View
import org.jsoup.nodes.Element
import java.util.*

open class AcceleraAbstractView(var element: Element, var view: View) {

    var id = UUID.nameUUIDFromBytes(element.toString().toByteArray())

    var descendents = mutableListOf<AcceleraAbstractView>()

    open fun applyAttributes() {}
}