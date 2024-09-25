package ai.accelera.library

import ai.accelera.library.api.AcceleraAPI
import ai.accelera.library.di.AcceleraDI
import ai.accelera.library.managers.LifecycleManager
import ai.accelera.library.pushes.PushNotificationManager
import ai.accelera.library.utils.AcceleraFirebase
import ai.accelera.library.utils.LogUtils
import ai.accelera.library.utils.LoggingExceptionHandler
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resumeWithException

object Accelera {

    /**
     * Used for determination app open from push
     */
    const val IS_OPENED_FROM_PUSH_BUNDLE_KEY = "isOpenedFromPush"

    private const val LOG_TAG_ACCELERA = "ACCELERA"

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.error(LOG_TAG_ACCELERA, throwable.message)
    }

    private val initScope = createAcceleraScope()
    private val acceleraScope = createAcceleraScope()

    private fun createAcceleraScope() = CoroutineScope(
        Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler,
    )

    private var firstInitCall: Boolean = true

    private lateinit var lifecycleManager: LifecycleManager

    // Контроллер который отправляет запросы на получение HTML и отравку логов
    private lateinit var api: AcceleraAPI

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
        LoggingExceptionHandler.runCatching {
            verifyMainThreadExecution("init")
            val currentProcessName = context.getCurrentProcessName()
            if (!context.isMainProcess(currentProcessName)) {
                Timber.w("Skip Accelera init not in main process! Current process $currentProcessName")
                return@runCatching
            }

            initComponents(context.applicationContext)
            Timber.i("init in $currentProcessName. firstInitCall: $firstInitCall, ")

            if (!firstInitCall) {
                InitializeLock.reset(InitializeLock.State.SAVE_ACCELERA_CONFIG)
            }

            // Api
            api = AcceleraAPI(acceleraConfig = configuration)

            initScope.launch {
                firstInitialization(context.applicationContext, configuration)
            }

            // Handle back app in foreground
            (context.applicationContext as? Application)?.apply {
                val applicationLifecycle = ProcessLifecycleOwner.get().lifecycle

                if (!Accelera::lifecycleManager.isInitialized) {
                    val activity = context as? Activity
                    val isApplicationResumed = applicationLifecycle.currentState == RESUMED
                    if (isApplicationResumed && activity == null) {
                        Timber.e("Incorrect context type for calling init in this place")
                    }
                    if (isApplicationResumed || context !is Application) {
                        Timber.w("We recommend to call Accelera.init() synchronously from Application.onCreate.")
                    }

                    Timber.i("init. init lifecycleManager")
                    lifecycleManager = LifecycleManager(
                        currentActivityName = activity?.javaClass?.name,
                        currentIntent = activity?.intent,
                        isAppInBackground = !isApplicationResumed,
                        onActivityStarted = { startedActivity ->

                        },
                        onActivityPaused = { pausedActivity ->

                        },
                        onActivityResumed = { resumedActivity ->

                        },
                        onActivityStopped = { resumedActivity ->

                        },
                        onTrackVisitReady = { source, requestUrl ->

                        }
                    )
                } else {
                    unregisterActivityLifecycleCallbacks(lifecycleManager)
                    applicationLifecycle.removeObserver(lifecycleManager)
                    lifecycleManager.wasReinitialized()
                }

                registerActivityLifecycleCallbacks(lifecycleManager)
                applicationLifecycle.addObserver(lifecycleManager)
            }
        }
    }

    private fun initComponents(context: Context) {
        AcceleraDI.init(context.applicationContext)
    }

    private suspend fun firstInitialization(
        context: Context,
        configuration: AcceleraConfiguration,
    ) = LoggingExceptionHandler.runCatchingSuspending {
        Timber.d("firstInitialization")
        val pushToken = withContext(acceleraScope.coroutineContext) {
            getToken()
        }
        updatePushToken(context, pushToken)
        Timber.d("firstInitialization pushToken: $pushToken")
    }

    private suspend fun getToken(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCanceledListener {
                continuation.resumeWithException(CancellationException())
            }
            .addOnSuccessListener { token ->
                continuation.resumeWith(Result.success(token))
            }
            .addOnFailureListener(continuation::resumeWithException)
    }

    fun updatePushToken(context: Context, token: String?) {
        Timber.d("updatePushToken token:$token")
        if (token == null) {
            return
        }
        if (::api.isInitialized) {
            api.updateUserInfo(
                token = token,
                completion = { jsonObject ->

                },
                onError = { exception ->

                },
            )
        }
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
     * @return true if notification is Accelera push and it's successfully handled, false otherwise.
     */
    fun handleRemoteMessage(
        context: Context,
        message: RemoteMessage?,
        channelId: String,
        channelName: String,
        @DrawableRes pushSmallIcon: Int,
        defaultActivity: Class<out Activity>,
        channelDescription: String? = null,
    ): Boolean = LoggingExceptionHandler.runCatching(defaultValue = false) {
        Timber.d(
            "handleRemoteMessage. channelId: $channelId, " +
                    "channelName: $channelName, channelDescription: $channelDescription, " +
                    "defaultActivity: ${defaultActivity.simpleName}, "
        )
        if (message == null) {
            Timber.d("handleRemoteMessage. Message is null.")
            return@runCatching false
        }
        val convertedMessage = AcceleraFirebase.convertToAcceleraRemoteMessage(message)
        if (convertedMessage == null) {
            return@runCatching false
        } else {
            Timber.d("handleRemoteMessage. ConvertedMessage: $convertedMessage")
        }

        runBlocking(acceleraScope.coroutineContext) {
            PushNotificationManager.handleRemoteMessage(
                context = context,
                remoteMessage = convertedMessage,
                channelId = channelId,
                channelName = channelName,
                pushSmallIcon = pushSmallIcon,
                channelDescription = channelDescription,
                activities = emptyMap(),
                defaultActivity = defaultActivity,
            )
        }
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

    /**
     * Creates and deliveries event of "Push clicked".
     * Recommended to be used with Accelera SDK pushes with [handleRemoteMessage] method.
     * Intent should contain "uniq_push_key" and "uniq_push_button_key" (optionally) in order to work correctly
     * Recommended call this method from background thread.
     *
     * @param context used to initialize the main tools
     * @param intent - intent recieved in app component
     *
     * @return true if Accelera SDK recognises push intent as Mindbox SDK push intent
     *         false if Accelera SDK cannot find critical information in intent
     */
    fun onPushClicked(
        context: Context,
        intent: Intent,
    ): Boolean = LoggingExceptionHandler.runCatching(defaultValue = false) {
        Timber.d("onPushClicked with intent")
        PushNotificationManager.getMessageIdFromPushIntent(intent)
            ?.let { uniqKey ->
                val pushButtonUniqKey = PushNotificationManager
                    .getUniqPushButtonKeyFromPushIntent(intent)
                onPushClicked(context, uniqKey, pushButtonUniqKey)
                true
            }
            ?: false
    }

    /**
     * Creates and deliveries event of "Push clicked". Recommended call this method from background
     * thread.
     *
     * @param context used to initialize the main tools
     * @param uniqKey - unique identifier of push notification
     * @param buttonUniqKey - unique identifier of push notification button
     */
    fun onPushClicked(
        context: Context,
        uniqKey: String,
        buttonUniqKey: String?,
    ) = LoggingExceptionHandler.runCatching {
        initComponents(context)
        Timber.d("onPushClicked. uniqKey: $uniqKey, buttonUniqKey: $buttonUniqKey")
        if (::api.isInitialized) {
            api.registerEvent(
                nameEvent = "clicked",
                messageId = uniqKey,
                completion = { jsonObject ->

                },
                onError = { exception ->

                },
            )
        }
    }

    /**
     * Обновить данные юзера
     *
     * @param userInfo - кастомные данные юзера
     */
    fun setUserInfo(userInfo: String?) {
        if (::api.isInitialized) {
            acceleraScope.launch {
                api.customData = userInfo

                val token = getToken()

                api.updateUserInfo(
                    token = token,
                    completion = { jsonObject ->

                    },
                    onError = { exception ->

                    },
                )
            }
        }
    }
}