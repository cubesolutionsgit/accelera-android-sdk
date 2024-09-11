package ai.accelera.library.utils

import android.util.Log
import ai.accelera.library.BuildConfig

object LogUtils {

    fun info(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun error(tag: String?, msg: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg.orEmpty())
        }
    }
}