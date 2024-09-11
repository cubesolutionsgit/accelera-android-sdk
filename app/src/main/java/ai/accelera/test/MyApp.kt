package ai.accelera.test

import ai.accelera.library.Accelera
import ai.accelera.library.AcceleraConfiguration
import android.app.Application

class MyApp : Application() {

    companion object {
        const val TEST_TOKEN = "AhKYDMGdmFtPswrVrBHZrSnVJHdRdzjCHqVAZrXVPXFVRHfT"
        const val TEST_URL = "https://flow2.accelera.ai"
        const val TEST_USER_ID = "bagman"
    }

    private val configuration = AcceleraConfiguration(
        token = TEST_TOKEN,
        url = TEST_URL,
        userId = TEST_USER_ID,
    )

    override fun onCreate() {
        super.onCreate()

        // Инициализация библиотеки
        Accelera.init(this, configuration)
    }
}