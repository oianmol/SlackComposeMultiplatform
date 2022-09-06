import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.swing.SwingDispatcher

actual fun MainDispatcher(): CoroutineDispatcher {
  return Dispatchers.Swing
}