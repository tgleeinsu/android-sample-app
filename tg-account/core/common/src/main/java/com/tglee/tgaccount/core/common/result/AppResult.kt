package com.tglee.tgaccount.core.common.result

/**
 * 성공/실패를 표현하는 단순 Result 래퍼.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val throwable: Throwable) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(throwable)
    return this
}

/** suspend 블록을 실행하고 결과를 AppResult 로 감싼다. CancellationException 은 다시 던진다. */
suspend inline fun <T> runCatchingResult(crossinline block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: kotlinx.coroutines.CancellationException) {
        throw e
    } catch (e: Throwable) {
        AppResult.Failure(e)
    }
