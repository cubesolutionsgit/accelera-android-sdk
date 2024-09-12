package ai.accelera.library.di.modules

import ai.accelera.library.utils.LoggingExceptionHandler
import android.content.pm.ApplicationInfo

internal fun AppModule(
    applicationContextModule: AppContextModule,
): AppModule = object : AppModule,
    AppContextModule by applicationContextModule {

    override fun isDebug(): Boolean = LoggingExceptionHandler.runCatching(defaultValue = false) {
        (applicationContextModule.appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}