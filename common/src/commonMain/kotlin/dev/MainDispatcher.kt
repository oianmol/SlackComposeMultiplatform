import kotlinx.coroutines.CoroutineDispatcher

expect val mainDispatcher: CoroutineDispatcher
expect val ioDispatcher: CoroutineDispatcher
expect val defaultDispatcher: CoroutineDispatcher