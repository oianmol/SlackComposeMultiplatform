package dev.baseio.slackclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*

class SlackNavigator(initialScreen: BackstackScreen) : Navigator {
    private val backStack: Deque<BackstackScreen> = LinkedList()
    override val changePublisher = Channel<Unit>()
    private val screenProviders = mutableMapOf<BackstackScreen, @Composable () -> Unit>()
    private val currentScreen: MutableState<BackstackScreen> by lazy {
        mutableStateOf(initialScreen)
    }

    private val navigatorScope = MainScope()

    override val screenCount: Int
        get() = backStack.size

    init {
        backStack.add(initialScreen)
    }

    override val totalScreens: Int
        get() = screenProviders.size

    override fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit) {
        screenProviders[screenTag] = screen
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

open class BackstackScreen(var name:String)

interface Navigator {
    val changePublisher : Channel<Unit>

    val totalScreens: Int
    val screenCount: Int

    fun navigateUp()
    fun navigate(screenTag: BackstackScreen)

    @Composable
    fun start()
    fun registerScreen(screenTag: BackstackScreen, screen: @Composable () -> Unit)
}