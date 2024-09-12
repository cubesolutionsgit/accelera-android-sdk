package ai.accelera.library.di

import ai.accelera.library.di.modules.AppContextModule
import ai.accelera.library.di.modules.AppModule
import android.app.Application
import android.content.Context
import timber.log.Timber

internal object AcceleraDI {

    internal lateinit var appModule: AppModule

    fun isInitialized() = AcceleraDI::appModule.isInitialized

    fun init(appContext: Context) {
        if (isInitialized()) return

        Timber.d("AcceleraDI init in ${Thread.currentThread().name}")

        val appContextModule = AppContextModule(
            application = appContext.applicationContext as Application
        )

        appModule = AppModule(
            applicationContextModule = appContextModule
        )
    }
}
