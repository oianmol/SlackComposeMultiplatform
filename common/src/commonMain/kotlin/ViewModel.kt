import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

open class ViewModel {
  val viewModelScope = CoroutineScope(Dispatchers.Main)
}