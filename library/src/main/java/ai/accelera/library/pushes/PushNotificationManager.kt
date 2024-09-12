package ai.accelera.library.pushes

import ai.accelera.library.Accelera
import ai.accelera.library.models.AcceleraRemoteMessage
import ai.accelera.library.models.PushAction
import ai.accelera.library.utils.Generator
import ai.accelera.library.utils.LoggingExceptionHandler
import android.app.*
import android.app.Notification.DEFAULT_ALL
import android.app.Notification.VISIBILITY_PRIVATE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import timber.log.Timber
import kotlin.random.Random

internal object PushNotificationManager {

    private const val EXTRA_NOTIFICATION_ID = "notification_id"
    private const val EXTRA_URL = "push_url"
    private const val EXTRA_UNIQ_PUSH_KEY = "uniq_push_key"
    private const val EXTRA_UNIQ_PUSH_BUTTON_KEY = "uniq_push_button_key"
    private const val EXTRA_PAYLOAD = "push_payload"
    private const val EXTRA_MESSAGE_ID = "message_id"

    private const val MAX_ACTIONS_COUNT = 3

    private fun buildLogMessage(
        message: AcceleraRemoteMessage,
        log: String,
    ): String = "Notify message ${message.uniqueKey}: $log"

    internal fun isNotificationsEnabled(
        context: Context,
    ): Boolean = LoggingExceptionHandler.runCatching(defaultValue = true) {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNotificationActive(
        notificationManager: NotificationManager,
        notificationId: Int,
    ): Boolean = LoggingExceptionHandler.runCatching(
        defaultValue = false,
    ) {
        notificationManager.activeNotifications.find { it.id == notificationId } != null
    }

    internal suspend fun handleRemoteMessage(
        context: Context,
        remoteMessage: AcceleraRemoteMessage,
        channelId: String,
        channelName: String,
        @DrawableRes pushSmallIcon: Int,
        channelDescription: String?,
        activities: Map<String, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
    ): Boolean = LoggingExceptionHandler.runCatchingSuspending(defaultValue = false) {

        Accelera.onPushReceived(
            context = context.applicationContext,
            uniqKey = remoteMessage.uniqueKey,
        )

        tryNotifyRemoteMessage(
            notificationId = Generator.generateUniqueInt(),
            context = context,
            remoteMessage = remoteMessage,
            channelId = channelId,
            channelName = channelName,
            pushSmallIcon = pushSmallIcon,
            channelDescription = channelDescription,
            activities = activities,
            defaultActivity = defaultActivity,
        )
        Timber.d("handleRemoteMessage success")
        true
    }

    private fun tryNotifyRemoteMessage(
        notificationId: Int,
        context: Context,
        remoteMessage: AcceleraRemoteMessage,
        channelId: String,
        channelName: String,
        @DrawableRes pushSmallIcon: Int,
        channelDescription: String?,
        activities: Map<String, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
    ) {
        val applicationContext = context.applicationContext

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notifyRemoteMessage(
            context = applicationContext,
            notificationManager = notificationManager,
            remoteMessage = remoteMessage,
            channelId = channelId,
            channelName = channelName,
            channelDescription = channelDescription,
            notificationId = notificationId,
            pushSmallIcon = pushSmallIcon,
            activities = activities,
            defaultActivity = defaultActivity,
            image = null,
        )
    }

    private fun isNotificationCancelled(
        notificationManager: NotificationManager,
        notificationId: Int,
    ) = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isNotificationActive(
        notificationManager,
        notificationId
    )

    private fun notifyRemoteMessage(
        context: Context,
        notificationManager: NotificationManager,
        remoteMessage: AcceleraRemoteMessage,
        channelId: String,
        channelName: String,
        channelDescription: String?,
        notificationId: Int,
        pushSmallIcon: Int,
        activities: Map<String, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
        image: Bitmap?,
    ) {
        createNotificationChannel(
            notificationManager = notificationManager,
            channelId = channelId,
            channelName = channelName,
            channelDescription = channelDescription,
        )
        val notification = buildNotification(
            context = context,
            notificationId = notificationId,
            uniqueKey = remoteMessage.uniqueKey,
            title = remoteMessage.title,
            text = remoteMessage.description,
            pushActions = remoteMessage.pushActions,
            pushLink = remoteMessage.pushLink,
            payload = remoteMessage.payload,
            messageId = remoteMessage.messageId,
            image = image,
            channelId = channelId,
            pushSmallIcon = pushSmallIcon,
            activities = activities,
            defaultActivity = defaultActivity,
        )
        notificationManager.notify(notificationId, notification)
    }

    private fun buildNotification(
        context: Context,
        notificationId: Int,
        messageId: String?,
        uniqueKey: String,
        title: String,
        text: String,
        pushActions: List<PushAction>,
        pushLink: String?,
        payload: String?,
        image: Bitmap?,
        channelId: String,
        @DrawableRes pushSmallIcon: Int,
        activities: Map<String, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
    ): Notification {
        val correctedLinksActivities = activities?.mapKeys { (key, _) ->
            key.replace("*", ".*").toRegex()
        }
        val hasButtons = pushActions.isNotEmpty()
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(pushSmallIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(DEFAULT_ALL)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .handlePushClick(
                context = context,
                notificationId = notificationId,
                uniqueKey = uniqueKey,
                payload = payload,
                pushLink = pushLink,
                activities = correctedLinksActivities,
                defaultActivity = defaultActivity,
                messageId = messageId,
            )
            .handleActions(
                context = context,
                notificationId = notificationId,
                uniqueKey = uniqueKey,
                payload = payload,
                pushActions = pushActions,
                activities = correctedLinksActivities,
                defaultActivity = defaultActivity,
                messageId = messageId,
            )
            .build()
    }

    internal fun getUniqKeyFromPushIntent(
        intent: Intent,
    ) = intent.getStringExtra(EXTRA_UNIQ_PUSH_KEY)

    internal fun getMessageIdFromPushIntent(
        intent: Intent,
    ) = intent.getStringExtra(EXTRA_MESSAGE_ID)

    internal fun getUniqPushButtonKeyFromPushIntent(
        intent: Intent,
    ) = intent.getStringExtra(EXTRA_UNIQ_PUSH_BUTTON_KEY)

    internal fun getUrlFromPushIntent(intent: Intent) = intent.getStringExtra(EXTRA_URL)

    internal fun getPayloadFromPushIntent(intent: Intent) = intent.getStringExtra(EXTRA_PAYLOAD)

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String,
        channelName: String,
        channelDescription: String?,
    ) = LoggingExceptionHandler.runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                channelDescription.let { description = it }
                lockscreenVisibility = VISIBILITY_PRIVATE
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(
        context: Context,
        activity: Class<out Activity>,
        id: Int,
        payload: String?,
        pushKey: String,
        messageId: String?,
        url: String?,
        pushButtonKey: String? = null,
    ): PendingIntent? = LoggingExceptionHandler.runCatching(defaultValue = null) {
        val intent = getIntent(
            context = context,
            activity = activity,
            id = id,
            payload = payload,
            pushKey = pushKey,
            messageId = messageId,
            url = url,
            pushButtonKey = pushButtonKey,
        )

        val flags = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        PendingIntent.getActivity(
            context,
            Random.nextInt(),
            intent,
            flags,
        )
    }

    private fun NotificationCompat.Builder.handlePushClick(
        context: Context,
        notificationId: Int,
        uniqueKey: String,
        payload: String?,
        pushLink: String?,
        messageId: String?,
        activities: Map<Regex, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
    ) = apply {
        val activity = resolveActivity(activities, pushLink, defaultActivity)
        createPendingIntent(
            context = context,
            activity = activity,
            id = notificationId,
            payload = payload,
            pushKey = uniqueKey,
            url = pushLink,
            messageId = messageId,
        )?.let(this::setContentIntent)
    }

    private fun NotificationCompat.Builder.handleActions(
        context: Context,
        notificationId: Int,
        uniqueKey: String,
        messageId: String?,
        payload: String?,
        pushActions: List<PushAction>,
        activities: Map<Regex, Class<out Activity>>?,
        defaultActivity: Class<out Activity>,
    ) = apply {
        runCatching {
            pushActions.take(MAX_ACTIONS_COUNT).forEach { pushAction ->
                val activity = resolveActivity(activities, pushAction.url, defaultActivity)
                createPendingIntent(
                    context = context,
                    activity = activity,
                    id = notificationId,
                    pushKey = uniqueKey,
                    payload = payload,
                    url = pushAction.url,
                    pushButtonKey = pushAction.uniqueKey,
                    messageId = messageId,
                )?.let { addAction(0, pushAction.text ?: "", it) }
            }
        }
    }

    private fun resolveActivity(
        activities: Map<Regex, Class<out Activity>>?,
        link: String?,
        defaultActivity: Class<out Activity>,
    ): Class<out Activity> {
        val key = link?.let { activities?.keys?.find { it.matches(link) } }
        return activities?.get(key) ?: defaultActivity
    }


    private fun getIntent(
        context: Context,
        activity: Class<*>,
        id: Int,
        payload: String?,
        pushKey: String,
        messageId: String?,
        url: String?,
        pushButtonKey: String?,
    ) = Intent(context, activity).apply {
        putExtra(EXTRA_PAYLOAD, payload)
        putExtra(Accelera.IS_OPENED_FROM_PUSH_BUNDLE_KEY, true)
        putExtra(EXTRA_NOTIFICATION_ID, id)
        putExtra(EXTRA_UNIQ_PUSH_KEY, pushKey)
        putExtra(EXTRA_UNIQ_PUSH_BUTTON_KEY, pushButtonKey)
        putExtra(EXTRA_MESSAGE_ID, messageId)
        url?.let { url -> putExtra(EXTRA_URL, url) }
        `package` = context.packageName
    }
}