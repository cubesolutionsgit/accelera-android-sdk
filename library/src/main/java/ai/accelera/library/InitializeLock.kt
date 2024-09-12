package ai.accelera.library

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import timber.log.Timber

internal object InitializeLock {

    private val map: MutableMap<State, CompletableDeferred<Unit>> = mutableMapOf(
        State.SAVE_ACCELERA_CONFIG to CompletableDeferred(),
        State.APP_STARTED to CompletableDeferred()
    )

    @WorkerThread
    internal suspend fun await(state: State) {
        State.entries.filter { state >= it }
            .sortedBy { it.ordinal }
            .mapNotNull { map[it] }
            .onEach {
                it.await()
            }
    }

    internal fun complete(state: State) {
        map[state]?.complete(Unit)
        Timber.i("State $state completed")
    }
    
    internal fun reset(state: State) {
        map[state]?.complete(Unit)
        map[state] = CompletableDeferred()
        Timber.i("State $state completed")
    }

    internal enum class State {
        SAVE_ACCELERA_CONFIG,
        APP_STARTED
    }
}

internal fun Job.initState(state: InitializeLock.State): Job {
    return this.apply {
        invokeOnCompletion {
            InitializeLock.complete(state)
        }
    }
}