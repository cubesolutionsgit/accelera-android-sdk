package ru.cubesolutions.inapplib

import android.view.View
import ru.cubesolutions.inapplib.api.AcceleraAPI
import ru.cubesolutions.inapplib.view.AcceleraBannerViewController
import ru.cubesolutions.inapplib.view.AcceleraViewDelegate
import java.lang.ref.WeakReference

interface AcceleraDelegate {
    fun bannerViewReady(bannerView: View, type: AcceleraBannerType)
    fun noBannerView()
    fun bannerViewClosed(): Boolean?
    fun bannerViewAction(action: String): Boolean?
}

class Accelera(config: AcceleraConfig) : AcceleraViewDelegate {

    companion object {
        const val JSON_STATUS = "status"
        const val JSON_TEMPLATE = "template"
        const val JSON_DATA = "data"
        const val JSON_TYPE = "type"
    }

    private var api: AcceleraAPI = AcceleraAPI(acceleraConfig = config)

    private var viewController = AcceleraBannerViewController()

    var delegate: WeakReference<AcceleraDelegate?>? = null

    fun loadBanner() {
        this.api.loadBanner({ jsonObject ->
            val statusResponse = jsonObject.optBoolean(JSON_STATUS)
            val html = jsonObject.optString(JSON_TEMPLATE)

            // Если template пустой или status false, то считаем что баннера нет
            if (!statusResponse || html.isNullOrEmpty()) {
                this.delegate?.get()?.noBannerView()
            }

            val data = jsonObject.optJSONObject(JSON_DATA)
            val type = data?.optString(JSON_TYPE)

            val bt = AcceleraBannerType.from(type)
            // Если мы не смогли распарсить тип банера, то по умолчанию берем тип TOP
            val bannerType = bt ?: AcceleraBannerType.TOP

            this.viewController.create(html = html, bannerType = bannerType)
        }, {

        })
    }

    override fun onReady(view: View, type: AcceleraBannerType) {

    }

    override fun onAdded() {

    }

    override fun onError(error: String) {
        this.delegate?.get()?.noBannerView()
    }

    override fun onAction(action: String) {

    }

    override fun onClose() {

    }
}