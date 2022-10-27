import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.testUseCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class DesktopAuthCreateWorkspaceUITest {
    @get:Rule
    val compose = createComposeRule()

    private val lifecycle = LifecycleRegistry()
    private val skKeyValueData = SKKeyValueData()
    private val rootComponent by lazy {
        RootComponent(DefaultComponentContext(lifecycle = lifecycle)).apply {
            navigateCreateWorkspace(true)
        }
    }

    lateinit var koinApplication: KoinApplication

    @Before
    fun prepare() {
        koinApplication = startKoin {
            modules(
                module {
                    single {
                        skKeyValueData
                    }
                    single {
                        SlackDB.invoke(DriverFactory().createDriver(SlackDB.Schema))
                    }
                },
                testUseCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                fakeDataSourceModule,
                testDispatcherModule
            )
        }
        Dispatchers.setMain(koinApplication.koin.get<CoroutineDispatcherProvider>().main)
    }


    @Test
    fun `create workspace fails when credentials are not entered`() {
        with(compose) {
            mainClock.autoAdvance = false
            setContent {
                val window = rememberWindowState()
                var rememberedComposeWindow by remember(window) {
                    mutableStateOf(WindowInfo(window.size.width, window.size.height))
                }
                LaunchedEffect(window) {
                    snapshotFlow { window.size }
                        .distinctUntilChanged()
                        .onEach {
                            rememberedComposeWindow = WindowInfo(it.width, it.height)
                        }
                        .launchIn(this)
                }
                DesktopApp(rememberedComposeWindow, {
                    rootComponent
                }, koinApplication)
            }
            onNodeWithText("Let me in...").performClick()
            waitUntilExists(hasText("check the form", substring = true, ignoreCase = true))
        }

    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        stopKoin()
    }
}

fun ComposeContentTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 1_000L
) {
    return this.waitUntilNodeCount(matcher, 1, timeoutMillis)
}

fun ComposeContentTestRule.waitUntilNodeCount(
    matcher: SemanticsMatcher,
    count: Int,
    timeoutMillis: Long = 1_000L
) {
    this.waitUntil(timeoutMillis) {
        this.onAllNodes(matcher).fetchSemanticsNodes().size == count
    }
}