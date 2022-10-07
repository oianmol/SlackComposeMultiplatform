import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val mainDispatcher: CoroutineDispatcher
  get() = Dispatchers.Main
actual val ioDispatcher: CoroutineDispatcher
  get() = Dispatchers.IO
actual val defaultDispatcher: CoroutineDispatcher
  get() = Dispatchers.Default