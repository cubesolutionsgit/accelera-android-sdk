package ai.accelera.library.inapp

import android.view.View
import ai.accelera.library.inapp.model.InAppBannerType

/**
 * Интерфейс с колбэками из Accelera
 */
interface InAppDelegate {

    /**
     * Баннер готов к паказу
     *
     * @param bannerView view для отображения баннера
     * @param type тип отображаемого баннера
     */
    fun bannerViewReady(bannerView: View, type: InAppBannerType)

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
