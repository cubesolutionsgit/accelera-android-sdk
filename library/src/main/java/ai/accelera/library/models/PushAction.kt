package ai.accelera.library.models

import com.google.gson.annotations.SerializedName
/**
 * A class representing accelera push action in [AcceleraRemoteMessage]
 *  * You can use it as a model to store data from accelera
 *  * with your custom push notification implementation.
 * */
data class PushAction(
    @SerializedName("uniqueKey") val uniqueKey: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("url") val url: String?,
)