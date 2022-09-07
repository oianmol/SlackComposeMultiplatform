import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.invoke
import org.jetbrains.skiko.SkikoDispatchers
import platform.Foundation.NSRunLoop
import platform.Foundation.performBlock
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext

actual fun MainDispatcher(): CoroutineDispatcher = SkikoDispatchers.Main
//NsQueueDispatcher(dispatch_get_main_queue())
internal class NsQueueDispatcher(private val dispatchQueue: dispatch_queue_t) : CoroutineDispatcher() {
  override fun dispatch(context: CoroutineContext, block: Runnable) {
    dispatch_async(dispatchQueue) { block.run() }
  }
}