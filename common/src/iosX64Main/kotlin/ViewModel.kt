import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel {
  actual val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
  protected actual open fun onCleared() {}
  fun clear() {
    onCleared()
    viewModelScope.cancel()
  }

}