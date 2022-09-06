import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun MainDispatcher(): CoroutineDispatcher {
  return Dispatchers.Main
}