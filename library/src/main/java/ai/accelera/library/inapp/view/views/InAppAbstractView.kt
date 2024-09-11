package ai.accelera.library.inapp.view.views

import android.view.View
import org.jsoup.nodes.Element
import java.util.*

open class InAppAbstractView(var element: Element, var view: View) {

    var id = UUID.nameUUIDFromBytes(element.toString().toByteArray())

    var descendents = mutableListOf<InAppAbstractView>()

    open fun applyAttributes() {}
}