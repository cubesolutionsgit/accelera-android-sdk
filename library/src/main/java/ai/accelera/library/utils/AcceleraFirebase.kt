package ai.accelera.library.utils

import ai.accelera.library.models.AcceleraRemoteMessage
import ai.accelera.library.models.PushAction
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * An object to use when choosing push provider in Mindbox.initPushServices or Mindbox.init.
 * Represents FCM
 * */
object AcceleraFirebase {

    /**
     * Converts [RemoteMessage] to [AcceleraRemoteMessage]
     * Use this method to get accelera push-notification data
     * It is encouraged to use this method inside try/catch block
     * @throws JsonSyntaxException – if remote message can't be parsed
     **/
    fun convertToAcceleraRemoteMessage(remoteMessage: RemoteMessage?): AcceleraRemoteMessage? {
        val pushActionsType = object : TypeToken<List<PushAction>>() {}.type

        return AcceleraRemoteMessage(
            messageId = remoteMessage?.data?.get(FirebaseMessage.MESSAGE_ID),
            uniqueKey = remoteMessage?.data?.get(FirebaseMessage.DATA_UNIQUE_KEY) ?: "",
            title = remoteMessage?.notification?.title ?: "",
            description = remoteMessage?.notification?.title ?: "",
            pushActions = emptyList(),
            pushLink = remoteMessage?.notification?.link?.toString(),
            imageUrl = remoteMessage?.data?.get(FirebaseMessage.DATA_IMAGE_URL),
            payload = remoteMessage?.data?.get(FirebaseMessage.DATA_PAYLOAD),
        )
    }
}