package ru.cubesolutions.inapplib.view

import android.view.View
import ru.cubesolutions.inapplib.model.AcceleraBannerType

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