package ai.accelera.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import timber.log.Timber
import java.util.*

object DeviceUtils {

    /**
     * Возвращает псевдо уникальный номер ID
     * Работает до Android 9
     * @return ID
     */
    val uniquePseudoID: String
        get() {
            val szDevIDShort = "27" + Build.BOARD.length % 10 + Build.BRAND.length % 10 +
                Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +
                Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 +
                Build.PRODUCT.length % 10
            var serial: String? = null
            try {
                serial = Build::class.java.getField("SERIAL").get(null).toString()
                return UUID(
                    szDevIDShort.hashCode().toLong(),
                    serial.hashCode().toLong()
                ).toString()
            } catch (exception: Exception) {
                Timber.e(exception)
                serial = "serial"
            }

            return UUID(szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }

    /**
     * Возвращает уникальный номер на основе Android Id
     * Если Android ID нет, то будет попытка генерации псевдоуникального номера
     * @return ID
     */
    @SuppressLint("HardwareIds")
    fun getUniquePseudoIDWithAndroidId(context: Context?): String {
        if (context == null) {
            return uniquePseudoID
        }
        try {
            val androidId: String? = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            if (androidId != null && androidId.isNotEmpty()) {
                return UUID.nameUUIDFromBytes(androidId.toByteArray()).toString()
            } else {
                return uniquePseudoID
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            return uniquePseudoID
        }
    }
}
