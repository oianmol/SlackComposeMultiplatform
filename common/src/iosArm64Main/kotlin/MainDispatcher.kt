import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext

actual val mainDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())
actual val ioDispatcher: CoroutineDispatcher = mainDispatcher
actual val defaultDispatcher: CoroutineDispatcher = mainDispatcher

//NsQueueDispatcher(dispatch_get_main_queue())
internal class NsQueueDispatcher(private val dispatchQueue: dispatch_queue_t) : CoroutineDispatcher() {
  override fun dispatch(context: CoroutineContext, block: Runnable) {
    dispatch_async(dispatchQueue) { block.run() }
  }
}