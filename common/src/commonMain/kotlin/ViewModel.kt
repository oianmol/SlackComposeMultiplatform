import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

open class ViewModel {
  val viewModelScope = CoroutineScope(SupervisorJob() +  MainDispatcher())
}