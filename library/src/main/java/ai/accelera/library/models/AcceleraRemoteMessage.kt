package ai.accelera.library.models
/**
 * A class representing Accelera remote message
 * You can use it as a model to store data from Accelera
 * with your custom push notification implementation.
 * */
data class AcceleraRemoteMessage(
    val messageId: String?,
    val uniqueKey: String,
    val title: String,
    val description: String,
    val pushActions: List<PushAction>,
    val pushLink: String?,
    val imageUrl: String?,
    val payload: String?,
)
