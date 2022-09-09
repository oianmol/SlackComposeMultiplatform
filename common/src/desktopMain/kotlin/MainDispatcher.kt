import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing

actual val mainDispatcher : CoroutineDispatcher = Dispatchers.Swing