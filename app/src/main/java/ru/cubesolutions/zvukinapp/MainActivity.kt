package ru.cubesolutions.zvukinapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.cubesolutions.inapplib.Accelera
import ru.cubesolutions.inapplib.AcceleraDelegate
import ru.cubesolutions.inapplib.AcceleraLib
import ru.cubesolutions.inapplib.model.AcceleraBannerType
import ru.cubesolutions.inapplib.model.AcceleraConfig
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

        accelera = AcceleraLib(
            config = AcceleraConfig(
                token = TEST_TOKEN,
                url = TEST_URL,
                userId = TEST_USER_ID,
            )
        )
        accelera?.delegate = WeakReference(this)
        accelera?.loadBanner()
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