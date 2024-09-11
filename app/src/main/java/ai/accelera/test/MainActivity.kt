package ai.accelera.test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ai.accelera.library.Accelera
import ai.accelera.library.AcceleraDelegate
import ai.accelera.library.AcceleraLib
import ai.accelera.library.model.AcceleraBannerType
import ai.accelera.library.model.AcceleraConfig
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), AcceleraDelegate {

    companion object {
        const val TEST_TOKEN = "AhKYDMGdmFtPswrVrBHZrSnVJHdRdzjCHqVAZrXVPXFVRHfT"
        const val TEST_URL = "https://flow2.accelera.ai"
        const val TEST_USER_ID = "bagman"

        const val LOG_TAG_TEST_APP = "TAG_TEST_APP"
    }

    var accelera: Accelera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    private fun initLibrary() {
        accelera = AcceleraLib(
            config = AcceleraConfig(
                token = TEST_TOKEN,
                url = TEST_URL,
                userId = TEST_USER_ID,
            )
        )
        accelera?.delegate = WeakReference(this)
    }

    private fun loadBannerTest() {
        accelera?.loadBanner(
            context = this,
            overrideBaseUrl = "https://flow2.accelera.ai",
        )
    }

    private fun logEventTest() {
        accelera?.logEvent(
            data = mapOf(
                "OFFER_TYPE_CD" to "GP TopUP"
            ),
        )
    }

    override fun bannerViewReady(bannerView: View, type: AcceleraBannerType) {
        Log.d(LOG_TAG_TEST_APP, "bannerViewReady")
        testDialog(bannerView)
    }

    private fun testDialog(bannerView: View) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        dialogBuilder.setView(bannerView)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun noBannerView() {
        Log.d(LOG_TAG_TEST_APP, "noBannerView")
    }

    override fun bannerViewClosed(): Boolean {
        Log.d(LOG_TAG_TEST_APP, "bannerViewAction")
        return true
    }

    override fun bannerViewAction(action: String): Boolean {
        Log.d(LOG_TAG_TEST_APP, "bannerViewAction $action")
        return true
    }
}