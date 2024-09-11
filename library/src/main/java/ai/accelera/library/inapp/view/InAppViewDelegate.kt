package ai.accelera.library.inapp.view

import android.view.View
import ai.accelera.library.inapp.model.InAppBannerType

/**
 * Интерфейс Accelera View
 */
interface InAppViewDelegate {
    fun onReady(view: View, type: InAppBannerType)
    fun onAdded()
    fun onError(error: String)
    fun onAction(action: String)
    fun onClose()
}