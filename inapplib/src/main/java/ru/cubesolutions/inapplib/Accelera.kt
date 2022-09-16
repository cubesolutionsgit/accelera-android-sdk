package ru.cubesolutions.inapplib

import android.content.Context
import java.lang.ref.WeakReference

/**
 * Интерфейс Accelera
 */
interface Accelera {

    var delegate: WeakReference<AcceleraDelegate?>?

    /**
     * Залогировать событие на сервер
     */
    fun logEvent(data: Map<String, Any>)

    /**
     * Начать загрузку баннера
     */
    fun loadBanner(context: Context)
}