import kotlinx.coroutines.CoroutineScope

import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope

actual abstract class ViewModel actual constructor() : AndroidXViewModel() {
  actual val viewModelScope: CoroutineScope
    get() = androidXViewModelScope

  actual open fun onClear() {
    super.onCleared()
  }
}