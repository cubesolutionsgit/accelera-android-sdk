package ai.accelera.test

import ai.accelera.library.Accelera
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ai.accelera.library.inapp.InApp
import ai.accelera.library.inapp.InAppDelegate
import ai.accelera.library.inapp.InAppImpl
import ai.accelera.library.inapp.model.InAppBannerType
import ai.accelera.library.AcceleraConfiguration
import android.content.Intent
import timber.log.Timber
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), InAppDelegate {

    companion object {
        const val TEST_TOKEN = "AhKYDMGdmFtPswrVrBHZrSnVJHdRdzjCHqVAZrXVPXFVRHfT"
        const val TEST_URL = "https://flow2.accelera.ai"
        const val TEST_USER_ID = "bagman"
    }

    private var innAppLibrary: InApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        processAcceleraIntent(intent)

        initLibrary()

        val buttonBanner = findViewById<Button>(R.id.button_load_banner)
        buttonBanner.setOnClickListener {
            loadBannerTest()
        }

        val buttonLogEvent = findViewById<Button>(R.id.button_log_event)
        buttonLogEvent.setOnClickListener {
            logEventTest()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        processAcceleraIntent(intent)
    }

    private fun processAcceleraIntent(intent: Intent?) {
        // Добавляем событие клика по push-уведомлению
        intent?.let { Accelera.onPushClicked(this, it) }
    }

    private fun initLibrary() {
        innAppLibrary = InAppImpl(
            config = AcceleraConfiguration(
                token = TEST_TOKEN,
                url = TEST_URL,
                userId = TEST_USER_ID,
            )
        )
        innAppLibrary?.delegate = WeakReference(this)
    }

    private fun loadBannerTest() {
        innAppLibrary?.loadBanner(
            context = this,
            overrideBaseUrl = "https://flow2.accelera.ai",
        )
    }

    private fun logEventTest() {
        innAppLibrary?.logEvent(
            data = mapOf(
                "OFFER_TYPE_CD" to "GP TopUP"
            ),
        )
    }

    override fun bannerViewReady(bannerView: View, type: InAppBannerType) {
        Timber.d("bannerViewReady")
        testDialog(bannerView)
    }

    private fun testDialog(bannerView: View) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        dialogBuilder.setView(bannerView)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun noBannerView() {
        Timber.d("noBannerView")
    }

    override fun bannerViewClosed(): Boolean {
        Timber.d("bannerViewAction")
        return true
    }

    override fun bannerViewAction(action: String): Boolean {
        Timber.d("bannerViewAction $action")
        return true
    }
}