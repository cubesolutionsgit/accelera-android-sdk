package ru.cubesolutions.inapplib

enum class AcceleraBannerType(val value: String) {
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

data class AcceleraConfig(val token: String, val url: String, val userId: String)