package ai.accelera.library.di.modules

import android.app.Application

internal sealed interface AcceleraModule

internal interface AppModule :
    AppContextModule {

    fun isDebug(): Boolean
}

internal interface AppContextModule : AcceleraModule {
    val appContext: Application
}

