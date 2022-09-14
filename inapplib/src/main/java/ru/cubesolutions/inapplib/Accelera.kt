package ru.cubesolutions.inapplib

import android.view.View
import ru.cubesolutions.inapplib.api.AcceleraAPI
import java.lang.ref.WeakReference

interface AcceleraDelegate {
    fun bannerViewReady(bannerView: View, type: AcceleraBannerType)
    fun noBannerView()
    fun bannerViewClosed(): Boolean?
    fun bannerViewAction(action: String): Boolean?
}

class Accelera(config: AcceleraConfig) {

    companion object {
        const val JSON_STATUS = "status"
        const val JSON_TEMPLATE = "template"
        const val JSON_DATA = "data"
        const val JSON_TYPE = "type"
    }

    private var api: AcceleraAPI = AcceleraAPI(acceleraConfig = config)

    var delegate: WeakReference<AcceleraDelegate?>? = null

    fun loadBanner() {
        this.api.loadBanner({ jsonObject ->
            val statusResponse = jsonObject.optBoolean(JSON_STATUS)
            val templateResponse = jsonObject.optString(JSON_TEMPLATE)

            // Если template пустой или status false, то считаем что баннера нет
            if (!statusResponse || templateResponse.isNullOrEmpty()) {
                delegate?.get()?.noBannerView()
            }

            val data = jsonObject.optJSONObject(JSON_DATA)
            val type = data?.optString(JSON_TYPE)

            val bt = AcceleraBannerType.from(type)
            // Если мы не смогли распарсить тип банера, то по умолчанию берем тип TOP
            var bannerType = bt ?: AcceleraBannerType.TOP


        }, {

        })
    }
}