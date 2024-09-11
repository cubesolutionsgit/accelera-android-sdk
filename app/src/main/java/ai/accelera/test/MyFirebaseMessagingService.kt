package ai.accelera.test

import ai.accelera.library.Accelera
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        // Активити по умолчанию. Откроется, если пришла ссылка, которой нет в перечислении
        val defaultActivity = MainActivity::class.java

        val channelId = "my_android_app_channel"
        val channelName = "Рекламные рассылки"
        val channelDescription = "Рассылки, которые содержат рекламу"
        val pushSmallIcon = R.mipmap.ic_launcher

        // Метод возвращает boolen, чтобы можно было сделать фолбек для обработки push-уведомлений
        val messageWasHandled = Accelera.handleRemoteMessage(
            context = applicationContext,
            message = remoteMessage,
            channelId = channelId, // Идентификатор канала для уведомлений, отправленных из Accelera
            channelName = channelName,
            pushSmallIcon = pushSmallIcon, // Маленькая иконка для уведомлений
            defaultActivity = defaultActivity,
            channelDescription = channelDescription
        )

        if (!messageWasHandled) {
            // Если push-уведомление было не от Mindbox или в нем некорректные данные,
            // то можно написать фолбек для его обработки
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        Accelera.updatePushToken(applicationContext, token)
    }
}