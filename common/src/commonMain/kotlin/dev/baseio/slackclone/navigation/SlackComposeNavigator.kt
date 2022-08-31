package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class SlackComposeNavigator(var initialScreen: BackstackScreen) : ComposeNavigator {
  var backStack: Deque<BackstackScreen> = LinkedList()
  var navigationResultMap = LinkedHashMap<NavigationKey, (Any) -> Unit>()
  override val changePublisher = Channel<Unit>()
  private val screenProviders = mutableMapOf<BackstackScreen, @Composable () -> Unit>()
  private val currentScreen: MutableState<BackstackScreen> by lazy {
    mutableStateOf(initialScreen)
  }

  private val navigatorScope = MainScope()
  override val lastScreen: BackstackScreen
    get() = currentScreen.value
  override val totalScreens: Int
    get() = screenProviders.size
  override val screenCount: Int
    get() = backStack.size

  init {
    backStack.add(initialScreen)
  }

  override fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit) {
    screenProviders[screenTag] = screen
  }

  override fun navigateBackWithResult(key: NavigationKey, data: Any, screen: BackstackScreen) {
    navigationResultMap[key]?.let {
      it(data)
      navigationResultMap.remove(key)// the result has been handled! we don't need to keep the callback
    }
  }

  override fun registerForNavigationResult(navigateChannel: NavigationKey, function: (Any) -> Unit) {
    navigationResultMap[navigateChannel] = function
  }

  override fun navigateUp() {
    if (backStack.size > 1) {
      backStack.pollLast()
      backStack.peek()?.let {
        currentScreen.value = it
        navigatorScope.launch {
          changePublisher.send(Unit)
        }
      }
    }
  }



  override fun navigate(screenTag: BackstackScreen) {
    backStack.add(screenTag)
    currentScreen.value = screenTag
    navigatorScope.launch(Dispatchers.Default) {
      changePublisher.send(Unit)
    }
  }

  @Composable
  override fun start() {
    screenProviders[currentScreen.value]?.invoke() ?: kotlin.run {
      throw IllegalArgumentException("Screen not found!")
    }
  }

}

open class BackstackScreen(var name: String)

interface ComposeNavigator {
  val lastScreen: BackstackScreen
  val changePublisher: Channel<Unit>

  val totalScreens: Int
  val screenCount: Int

  fun navigateUp()
  fun navigate(screenTag: BackstackScreen)

  @Composable
  fun start()
  fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit)
  fun navigateBackWithResult(
    key: NavigationKey,
    data: Any,
    screen: BackstackScreen
  )

  fun registerForNavigationResult(navigateChannel: NavigationKey, function: (Any) -> Unit)
}