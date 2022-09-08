import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

abstract class ViewModel {
  val viewModelScope = CoroutineScope(SupervisorJob() + MainDispatcher())
}