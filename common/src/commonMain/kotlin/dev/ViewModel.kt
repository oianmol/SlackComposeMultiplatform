import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
  val viewModelScope: CoroutineScope
  open fun onClear()
}