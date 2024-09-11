package ai.accelera.library.view

import android.view.View
import ai.accelera.library.model.AcceleraBannerType

/**
 * Интерфейс Accelera View
 */
interface AcceleraViewDelegate {
    fun onReady(view: View, type: AcceleraBannerType)
    fun onAdded()
    fun onError(error: String)
    fun onAction(action: String)
    fun onClose()
}