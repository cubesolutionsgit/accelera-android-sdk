package ru.cubesolutions.inapplib

import android.view.View
import ru.cubesolutions.inapplib.model.AcceleraBannerType

/**
 * Интерфейс с колбэками из Accelera
 */
interface AcceleraDelegate {

    /**
     * Баннер готов к паказу
     *
     * @param bannerView view для отображения баннера
     * @param type тип отображаемого баннера
     */
    fun bannerViewReady(bannerView: View, type: AcceleraBannerType)

    /**
     * Баннера нет
     */
    fun noBannerView()

    /**
     * Баннер был закрыт
     */
    fun bannerViewClosed(): Boolean?

    /**
     * На баннер нажали
     *
     * @param action данные вовзращаемые из кнопки
     */
    fun bannerViewAction(action: String): Boolean?
}
