package ru.cubesolutions.zvukinapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import ru.cubesolutions.inapplib.Accelera
import ru.cubesolutions.inapplib.AcceleraBannerType
import ru.cubesolutions.inapplib.AcceleraConfig
import ru.cubesolutions.inapplib.AcceleraDelegate
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), AcceleraDelegate {

    companion object {
        const val LOG_TAG_TEST_APP = "TAG_TEST_APP"
    }

    var accelera: Accelera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelera = Accelera(
            config = AcceleraConfig(
                token = "AhKYDMGdmFtPswrVrBHZrSnVJHdRdzjCHqVAZrXVPXFVRHfT",
                url = "https://flow2.accelera.ai",
                userId = "bagman"
            )
        )
        accelera?.delegate = WeakReference(this)
        accelera?.loadBanner()
    }

    override fun bannerViewReady(bannerView: View, type: AcceleraBannerType) {
        Log.d(LOG_TAG_TEST_APP, "bannerViewReady")
    }

    override fun noBannerView() {
        Log.d(LOG_TAG_TEST_APP, "noBannerView")
    }

    override fun bannerViewClosed(): Boolean? {
        Log.d(LOG_TAG_TEST_APP, "bannerViewAction")
        return true
    }

    override fun bannerViewAction(action: String): Boolean? {
        Log.d(LOG_TAG_TEST_APP, "bannerViewAction $action")
        return true
    }
}