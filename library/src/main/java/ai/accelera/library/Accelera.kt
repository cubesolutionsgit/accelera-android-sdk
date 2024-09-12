package ai.accelera.library

import ai.accelera.library.di.AcceleraDI
import ai.accelera.library.managers.LifecycleManager
import ai.accelera.library.utils.LogUtils
import ai.accelera.library.utils.LoggingExceptionHandler
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
     * @return true if notification is Accelera push and it's successfully handled, false otherwise.
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