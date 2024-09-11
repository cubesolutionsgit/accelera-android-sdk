package ai.accelera.library

import ai.accelera.library.utils.LogUtils
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object Accelera {

    private const val LOG_TAG_ACCELERA = "ACCELERA"

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.error(LOG_TAG_ACCELERA, throwable.message)
    }

    internal val acceleraScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler,
    )

    @MainThread
    fun init(
        application: Application,
        configuration: AcceleraConfiguration,
    ) {
        initialize(application, configuration)
    }

    private fun initialize(
        context: Context,
        configuration: AcceleraConfiguration,
    ) {

    }

    fun updatePushToken(context: Context, token: String) {

    }

    /**
     * Handles only Accelera notification message from [FirebaseMessageServise].
     *
     * @param context context used for Accelera initializing and push notification showing
     * @param message the [RemoteMessage] received from Firebase or HMS
     * @param channelId the id of channel for Accelera pushes
     * @param channelName the name of channel for Accelera pushes
     * @param pushSmallIcon icon for push notification as drawable resource
     * @param channelDescription the description of channel for Accelera pushes. Default is null
     * @param defaultActivity default activity to be opened if url was not found in [activities]
     *
     * @return true if notification is Mindbox push and it's successfully handled, false otherwise.
     */
    fun handleRemoteMessage(
        context: Context,
        message: Any?,
        channelId: String,
        channelName: String,
        @DrawableRes pushSmallIcon: Int,
        defaultActivity: Class<out Activity>,
        channelDescription: String? = null,
    ): Boolean {
        return true
    }

    /**
     * Creates and deliveries event of "Push delivered". Recommended call this method from
     * background thread.
     *
     * Use this method only if you have custom push handling you don't use [Accelera.handleRemoteMessage].
     * You must not call it otherwise.
     *
     * @param context used to initialize the main tools
     * @param uniqKey - unique identifier of push notification
     */
    fun onPushReceived(context: Context, uniqKey: String) {

    }
}