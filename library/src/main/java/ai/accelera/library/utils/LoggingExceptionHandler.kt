package ai.accelera.library.utils

internal object LoggingExceptionHandler : ExceptionHandler() {

    override fun handle(exception: Throwable) {
        try {
            // TODO: Добавить сбор логов для анализа
        } catch (e: Throwable) {
            println(e.message)
        }
    }
}

internal fun <T> loggingRunCatching(
    defaultValue: T,
    block: () -> T,
): T = LoggingExceptionHandler.runCatching(defaultValue, block)

internal fun <T> loggingRunCatching(block: () -> T) = LoggingExceptionHandler.runCatching(block)

