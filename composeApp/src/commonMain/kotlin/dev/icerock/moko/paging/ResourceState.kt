package dev.icerock.moko.paging

sealed class ResourceState<out T, out E> {
    data class Success<out T, out E>(val data: T) : ResourceState<T, E>()
    data class Failed<out T, out E>(val error: E) : ResourceState<T, E>()
    class Loading<out T, out E> : ResourceState<T, E>()
    class Empty<out T, out E> : ResourceState<T, E>()

    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Success
    fun isEmpty(): Boolean = this is Empty
    fun isFailed(): Boolean = this is Failed

    fun dataValue(): T? = (this as? Success)?.data

    fun errorValue(): E? = (this as? Failed)?.error
}

fun <T, E> T.asState(): ResourceState<T, E> =
    ResourceState.Success(this)

fun <T, E> T?.asState(whenNull: () -> ResourceState<T, E>): ResourceState<T, E> =
    this?.asState() ?: whenNull()

fun <T, E> List<T>.asState(): ResourceState<List<T>, E> = if (this.isEmpty()) {
    ResourceState.Empty()
} else {
    ResourceState.Success(this)
}

fun <T, E> List<T>?.asState(whenNull: () -> ResourceState<List<T>, E>): ResourceState<List<T>, E> =
    this?.asState() ?: whenNull()

inline fun <reified T, reified E> ResourceState<T, E>?.nullAsEmpty(): ResourceState<T, E> =
    this ?: ResourceState.Empty()

inline fun <reified T, reified E> ResourceState<T, E>?.nullAsLoading(): ResourceState<T, E> =
    this ?: ResourceState.Loading()