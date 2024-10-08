package ai.accelera.library.inapp

import android.content.Context
import java.lang.ref.WeakReference

/**
 * Интерфейс InApp library
 * используется для работы с инн апп логикой
 */
interface InApp {

    var delegate: WeakReference<InAppDelegate?>?

    /**
     * Залогировать событие на сервер
     *
     * @param data данные для отправки аналитики
     * @param overrideBaseUrl если необходимо использовать не базовый url из конфига, можно явно указать другой адрес сервера
     */
    fun logEvent(
        data: Map<String, Any>,
        overrideBaseUrl: String? = null,
    )

    /**
     * Начать загрузку баннера
     *
     * @param context контекст приложения для создания view элементов на экране
     * @param overrideBaseUrl если необходимо использовать не базовый url из конфига, можно явно указать другой адрес сервера
     */
    fun loadBanner(
        context: Context,
        overrideBaseUrl: String? = null,
    )
}