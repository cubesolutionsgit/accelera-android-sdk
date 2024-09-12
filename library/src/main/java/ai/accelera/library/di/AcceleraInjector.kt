package ai.accelera.library.di

import ai.accelera.library.di.modules.AppModule
import kotlin.reflect.KProperty

internal fun <T> acceleraInject(initializer: AppModule.() -> T) = AcceleraInjector(initializer)

internal class AcceleraInjector<T>(private val initializer: AppModule.() -> T) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return initializer.invoke(AcceleraDI.appModule)
    }
}
