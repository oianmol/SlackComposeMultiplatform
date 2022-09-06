package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.LinkedHashMap

class SlackComposeNavigator : ComposeNavigator {
  private var backStackRoute: LinkedHashMap<BackstackRoute, Deque<BackstackScreen>> =
    LinkedHashMap()
  private var navigationResultMap = LinkedHashMap<String, (Any) -> Unit>()

  private val currentRoute: MutableState<BackstackRoute?> = mutableStateOf(null)
  private val currentScreen: MutableState<BackstackScreen?> = mutableStateOf(null)

  private val screenProviders = mutableMapOf<BackstackScreen, @Composable () -> Unit>()
  private val navigatorScope = CoroutineScope(Dispatchers.Main)
  override val changePublisher = Channel<Unit>()

  override val lastScreen: BackstackScreen?
    get() = currentScreen.value
  override val totalScreens: Int
    get() = screenProviders.size
  override val screenCount: Int
    get() {
      var count = 0
      backStackRoute.forEach { count += it.value.size }
      return count
    }

  @Composable
  override fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit) {
    screenProviders[screenTag] = screen
  }


  @Composable
  override fun route(route: BackstackRoute, function: @Composable () -> Unit) {
    backStackRoute[route] = LinkedList()
    function()
  }

  override fun deliverResult(key: NavigationKey, data: Any, screen: BackstackScreen) {
    navigationResultMap[key.key.plus(screen.name)]?.let {
      it(data)
      navigationResultMap.remove(key.key.plus(screen.name))// the result has been handled! we don't need to keep the callback
    }
  }

  override fun registerForNavigationResult(navigateChannel: NavigationKey,backstackScreen: BackstackScreen, function: (Any) -> Unit) {
    navigationResultMap[navigateChannel.key.plus(backstackScreen.name)] = function
  }

  override fun navigateUp() {
    backStackRoute[currentRoute.value]?.poll()
    if (backStackRoute[currentRoute.value]?.isEmpty() == true) {
      // if the route has empty screens then remove the route entirely
      backStackRoute.remove(currentRoute.value)
    }
    backStackRoute[currentRoute.value]?.peek()?.let {
      currentScreen.value = it
      navigatorScope.launch {
        changePublisher.send(Unit)
      }
    }
  }

  override fun navigateRoute(route: BackstackRoute) {
    currentRoute.value = route
    currentScreen.value = route.initialScreen
    backStackRoute[currentRoute.value]?.push(route.initialScreen)
    navigatorScope.launch {
      changePublisher.send(Unit)
    }
  }


  override fun navigateScreen(screenTag: BackstackScreen) {
    backStackRoute[currentRoute.value]?.push(screenTag)
    currentScreen.value = screenTag
    navigatorScope.launch {
      changePublisher.send(Unit)
    }
  }

  @Composable
  override fun start(route: BackstackRoute) {
    if (currentScreen.value == null) {
      currentScreen.value = route.initialScreen
      currentRoute.value = route
      backStackRoute[currentRoute.value]?.push(route.initialScreen)
    }
    screenProviders[currentScreen.value]?.invoke() ?: kotlin.run {
      throw IllegalArgumentException("Screen not found!")
    }
  }


}

open class BackstackScreen(var name: String)
open class BackstackRoute(var name: String, var initialScreen: BackstackScreen)

interface ComposeNavigator {
  val lastScreen: BackstackScreen?
  val changePublisher: Channel<Unit>

  val totalScreens: Int
  val screenCount: Int

  fun navigateUp()
  fun navigateScreen(screenTag: BackstackScreen)
  fun navigateRoute(route: BackstackRoute)

  @Composable
  fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit)
  fun deliverResult(
    key: NavigationKey,
    data: Any,
    screen: BackstackScreen
  )

  fun registerForNavigationResult(navigateChannel: NavigationKey,backstackScreen: BackstackScreen, function: (Any) -> Unit)

  @Composable
  fun route(route: BackstackRoute, function: @Composable () -> Unit)

  @Composable
  fun start(route: BackstackRoute)
}