package ai.accelera.library.inapp.model

/**
 * Тип баннера для отображения на экране
 *
 * @param value значение которые приходят с сервера
 */
enum class InAppBannerType(val value: String) {
    NOTIFICATION("notification"),
    TOP("top"),
    CENTER("center"),
    FULLSCREEN("fullscreen");

    companion object {
        fun from(findValue: String?) = values().firstOrNull() {
            it.value == findValue?.lowercase()
        }
    }
}