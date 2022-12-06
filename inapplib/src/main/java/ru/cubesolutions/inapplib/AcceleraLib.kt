package ru.cubesolutions.inapplib

import android.content.Context
import android.view.View
import kotlinx.coroutines.*
import ru.cubesolutions.inapplib.api.AcceleraAPI
import ru.cubesolutions.inapplib.model.AcceleraBannerType
import ru.cubesolutions.inapplib.model.AcceleraConfig
import ru.cubesolutions.inapplib.utils.LogUtils
import ru.cubesolutions.inapplib.view.AcceleraBannerViewController
import ru.cubesolutions.inapplib.view.AcceleraViewDelegate
import java.lang.ref.WeakReference

/**
 * Класс для работы загрузки баннера
 *
 * @param config конфигурация работы класса. Необходим для запросов в сеть.
 */
class AcceleraLib(
    config: AcceleraConfig,
) : AcceleraViewDelegate, Accelera {

    companion object {
        const val JSON_STATUS = "status"
        const val JSON_TEMPLATE = "template"
        const val JSON_DATA = "data"
        const val JSON_TYPE = "type"

        const val TAG_IN_APP_ACCELERA = "IN_APP_ACCELERA"
    }

    // Контроллер который отправляет запросы на получение HTML и отравку логов
    private var api: AcceleraAPI = AcceleraAPI(acceleraConfig = config)

    // View контроллер, который отвечает за парсинг HTML и создания из него VIEW
    private var viewController = AcceleraBannerViewController()

    // Интерфейс для получения данных из Accelera
    override var delegate: WeakReference<AcceleraDelegate?>? = null

    init {
        viewController.delegate = WeakReference(this)
    }

    private val job = Job()
    private val name = CoroutineName("in app accelera scope")
    private val scope: CoroutineScope = CoroutineScope(job + name + Dispatchers.IO)

    override fun logEvent(
        data: Map<String, Any>,
        overrideBaseUrl: String?,
    ) {
        // TODO: cache if network is not available
        this.api.logEvent(
            data = data,
            completion = { jsonObject ->
                LogUtils.info(
                    TAG_IN_APP_ACCELERA,
                    "logEvent jsonObject - $jsonObject"
                )
            },
            onError = { exception ->
                LogUtils.error(
                    TAG_IN_APP_ACCELERA,
                    "logEvent exception - ${exception.localizedMessage}"
                )
            },
            overrideBaseUrl = overrideBaseUrl,
        )
    }

    override fun loadBanner(
        context: Context,
        overrideBaseUrl: String?,
    ) {
        LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner")
        this.api.loadBanner(
            completion = { jsonObject ->
                LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner jsonObject - $jsonObject")

                // Получаем из JSON файла статус и HTML страницу
                val statusResponse = jsonObject.optBoolean(JSON_STATUS)
                LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner statusResponse - $statusResponse")
                val html = jsonObject.optString(JSON_TEMPLATE)
                LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner html - $html")

                // Если template пустой или status false, то считаем что баннера нет
                if (!statusResponse || html.isNullOrEmpty()) {
                    LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner delegate noBannerView")
                    delegate?.get()?.noBannerView()
                }

                // Получаем из JSON файла данные по тип баннера
                val data = jsonObject.optJSONObject(JSON_DATA)
                LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner data $data")
                val type = data?.optString(JSON_TYPE)
                LogUtils.info(TAG_IN_APP_ACCELERA, "loadBanner type $type")

                // Пробуем найти из ответа сервера нужный тип баннера
                val bt = AcceleraBannerType.from(type)

                // Если мы не смогли распарсить тип банера, то по умолчанию берем тип TOP
                val bannerType = bt ?: AcceleraBannerType.TOP

                // Все представления будут создаваться в этом потоке
                scope.launch {
                    viewController.create(
                        context = context,
                        html = html,
                        bannerType = bannerType
                    )
                }
            },
            onError = { exception ->
                LogUtils.error(
                    TAG_IN_APP_ACCELERA,
                    "loadBanner exception - ${exception.localizedMessage}"
                )
            },
            overrideBaseUrl = overrideBaseUrl,
        )
    }

    override fun onReady(view: View, type: AcceleraBannerType) {
        LogUtils.info(TAG_IN_APP_ACCELERA, "onReady type - $type")
        scope.launch(Dispatchers.Main) {
            delegate?.get()?.bannerViewReady(bannerView = view, type = type)
        }
    }

    override fun onAdded() {
        LogUtils.info(TAG_IN_APP_ACCELERA, "onAdded")
        this.logEvent(data = mapOf("event" to "shown"))
    }

    override fun onError(error: String) {
        LogUtils.error(TAG_IN_APP_ACCELERA, "onError error - $error")
        delegate?.get()?.noBannerView()
    }

    override fun onAction(action: String) {
        LogUtils.info(TAG_IN_APP_ACCELERA, "onAction action - $action")
        val closeAutomatically = delegate?.get()?.bannerViewAction(action = action)
        if (closeAutomatically == true) {
            removeFromSuperview()
        }
        viewController.clear()
    }

    private fun removeFromSuperview() {
        try {
//            val view: View? = viewController.view
//            val viewParent: ViewGroup? = view?.parent as ViewGroup?
//            viewParent?.removeView(view)
        } catch (exception: Exception) {
            LogUtils.error(
                TAG_IN_APP_ACCELERA,
                "onAction error - ${exception.localizedMessage}"
            )
        }
    }

    override fun onClose() {
        LogUtils.info(TAG_IN_APP_ACCELERA, "onClose")
        this.logEvent(data = mapOf("event" to "closed"))
    }
}