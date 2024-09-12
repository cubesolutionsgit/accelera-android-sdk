package ai.accelera.library

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Looper
import android.os.Process
import timber.log.Timber

internal fun Context.isMainProcess(processName: String?): Boolean {
    val mainProcessName = getString(R.string.accelera_android_process).ifBlank { packageName }
    return processName?.equalsAny(
        mainProcessName,
        "$packageName:$mainProcessName",
        "$packageName$mainProcessName",
    ) ?: false
}

internal fun Context.getCurrentProcessName(): String? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return Application.getProcessName()
    }

    val mypid = Process.myPid()
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processes = manager.runningAppProcesses

    return processes.firstOrNull { info -> info.pid == mypid }?.processName
}

internal fun String?.equalsAny(vararg values: String, ignoreCase: Boolean = false): Boolean = values.any { this?.equals(it, ignoreCase) == true }
internal inline fun <reified T : Enum<T>> String?.enumValue(default: T? = null): T {
    return this?.let {
        enumValues<T>().firstOrNull { value ->
            value.name
                .replace("_", "")
                .equals(
                    this.replace("_", "").trim(),
                    ignoreCase = true
                )
        }
    } ?: default ?: throw IllegalArgumentException("Value for $this could not be found")
}

internal fun verifyMainThreadExecution(methodName: String) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        Timber.e("Method $methodName must be called by main thread")
    }
}
