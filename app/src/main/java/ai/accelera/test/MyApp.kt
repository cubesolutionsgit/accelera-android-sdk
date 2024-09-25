package ai.accelera.test

import ai.accelera.library.Accelera
import ai.accelera.library.AcceleraConfiguration
import android.app.Application
import timber.log.Timber

class MyApp : Application() {

    companion object {
        const val TEST_TOKEN = "AhKYDMGdmFtPswrVrBHZrSnVJHdRdzjCHqVAZrXVPXFVRHfT"
        const val TEST_URL = "https://g1-dev.accelera.ai"
        const val TEST_USER_ID = "bagman"
    }

    private val configuration = AcceleraConfiguration(
        token = TEST_TOKEN,
        url = TEST_URL,
        userIdInApp = TEST_USER_ID,
    )

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Инициализация библиотеки
        Accelera.init(this, configuration)
    }
}