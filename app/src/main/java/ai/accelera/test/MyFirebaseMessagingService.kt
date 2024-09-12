package ai.accelera.test

import ai.accelera.library.Accelera
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessageReceived remoteMessage:$remoteMessage")
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
            // Если push-уведомление было не от Accelera или в нем некорректные данные,
            // то можно написать фолбек для его обработки
        }
    }

    override fun onNewToken(token: String) {
        Timber.d("onNewToken token:$token")
        Accelera.updatePushToken(applicationContext, token)
    }
}