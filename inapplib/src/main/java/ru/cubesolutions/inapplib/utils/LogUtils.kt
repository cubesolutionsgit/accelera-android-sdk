package ru.cubesolutions.inapplib.utils

import android.util.Log
import ru.cubesolutions.inapplib.BuildConfig

object LogUtils {

    fun info(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun error(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }
}