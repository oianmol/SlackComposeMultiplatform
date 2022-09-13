package dev.baseio.slackclone.navigation

import mainDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.collections.LinkedHashMap

class SlackComposeNavigator : ComposeNavigator {
  private var backStackRoute: LinkedHashMap<BackstackRoute, ArrayDeque<BackstackScreen>> =
    LinkedHashMap()
  private var navigationResultMap = LinkedHashMap<String, (Any) -> Unit>()
  private val backPressObserver = HashMap<BackstackScreen, () -> Unit>()

  private val currentRoute: MutableState<BackstackRoute?> = mutableStateOf(null)
  private val currentScreen: MutableState<BackstackScreen?> = mutableStateOf(null)

  private val screenProviders = mutableMapOf<BackstackScreen, @Composable () -> Unit>()
  private val navigatorScope = CoroutineScope(SupervisorJob() + mainDispatcher)
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
    if (!backStackRoute.containsKey(route)) {
      backStackRoute[route] = ArrayDeque()
      function()
    }
  }

  override fun deliverResult(key: NavigationKey, data: Any, screen: BackstackScreen) {
    navigationResultMap[key.key.plus(screen.name)]?.let {
      it(data)
      navigationResultMap.remove(key.key.plus(screen.name))// the result has been handled! we don't need to keep the callback
    }
  }

  override fun observeWhenBackPressedFor(screen: BackstackScreen, function: () -> Unit) {
    backPressObserver[screen] = function
  }

  override fun removeObserverForBackPress(screen: BackstackScreen) {
    backPressObserver.remove(screen)
  }

  override fun registerForNavigationResult(
    navigateChannel: NavigationKey,
    backstackScreen: BackstackScreen,
    function: (Any) -> Unit
  ) {
    navigationResultMap[navigateChannel.key.plus(backstackScreen.name)] = function
  }

  override var whenRouteCanNoLongerNavigateBack: () -> Unit = {}

  override fun navigateUp() {
    // this handles backPressObserver
    currentScreen.value?.let {
      if (backPressObserver.containsKey(it)) {
        backPressObserver[it]?.invoke()
        backPressObserver.remove(it)
        return
      }
    }

    backStackRoute[currentRoute.value]?.remove(currentScreen.value)
    // now if the current route becomes empty set the current route to the previous route
    checkWhenWeCantNavigateBack()
    backStackRoute[currentRoute.value]?.lastOrNull()?.let {
      setCurrentScreenAndNotify(it)
    } ?: run {
      whenRouteCanNoLongerNavigateBack.invoke()
    }
  }

  private fun setCurrentScreenAndNotify(screen: BackstackScreen) {
    currentScreen.value = screen
    navigatorScope.launch {
      changePublisher.send(Unit)
    }
  }

  private fun checkWhenWeCantNavigateBack() {
    if (backStackRoute[currentRoute.value]?.isEmpty() == true) {
      currentRoute.value = backStackRoute.keys.toTypedArray()[backStackRoute.size.minus(2)]
      if (backStackRoute[currentRoute.value]?.isEmpty() == true) {
        // if the prev route is also empty then invoke system back press
        // then invoke system close the app
        whenRouteCanNoLongerNavigateBack.invoke()
      }
    }
  }

  override fun navigateRoute(route: BackstackRoute, clearRoutes: (BackstackRoute, () -> Unit) -> Unit) {
    backStackRoute.keys.forEach { backstackRoute ->
      clearRoutes(backstackRoute) { // if the user clears the onboarding route
        backStackRoute[backstackRoute]?.clear()
      }
    }
    currentRoute.value = route
    navigateScreen(route.initialScreen)
  }


  override fun navigateScreen(screenTag: BackstackScreen) {
    backStackRoute[currentRoute.value]?.add(screenTag)
    setCurrentScreenAndNotify(screenTag)
  }

  @Composable
  override fun start(route: BackstackRoute) {
    if (currentScreen.value == null) {
      currentRoute.value = route
      currentScreen.value = route.initialScreen
      backStackRoute[currentRoute.value]?.add(route.initialScreen)
    }
    screenProviders[currentScreen.value]?.invoke() ?: kotlin.run {
      throw IllegalArgumentException("Screen not found!")
    }
  }


}

open class BackstackScreen(var name: String)
open class BackstackRoute(var name: String, var initialScreen: BackstackScreen)

interface ComposeNavigator {
  var whenRouteCanNoLongerNavigateBack: () -> Unit
  val lastScreen: BackstackScreen?
  val changePublisher: Channel<Unit>

  val totalScreens: Int
  val screenCount: Int

  fun navigateUp()
  fun navigateScreen(screenTag: BackstackScreen)
  fun navigateRoute(route: BackstackRoute, removeRoute: (BackstackRoute, () -> Unit) -> Unit)

  @Composable
  fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit)
  fun deliverResult(
    key: NavigationKey,
    data: Any,
    screen: BackstackScreen
  )

  fun registerForNavigationResult(
    navigateChannel: NavigationKey,
    backstackScreen: BackstackScreen,
    function: (Any) -> Unit
  )

  @Composable
  fun route(route: BackstackRoute, function: @Composable () -> Unit)

  @Composable
  fun start(route: BackstackRoute)
  fun observeWhenBackPressedFor(screen: BackstackScreen, function: () -> Unit)
  fun removeObserverForBackPress(screen: BackstackScreen)
}