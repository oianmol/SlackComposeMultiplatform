import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel {
  actual val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
  actual open fun onClear() {}
  fun clear() {
    onClear()
    viewModelScope.cancel()
  }

}