import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.android.MobileApp
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.testUseCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class AndroidAuthCreateWorkspaceUITest {
    @get:Rule
    val compose = createComposeRule()

    private val lifecycle = LifecycleRegistry()
    private val skKeyValueData = SKKeyValueData(ApplicationProvider.getApplicationContext())
    private val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)).apply {
        navigateCreateWorkspace(true)
    } }
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
                        SlackDB.invoke(DriverFactory(ApplicationProvider.getApplicationContext()).createDriver(SlackDB.Schema))
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
    fun whenLoginToSlackClickedItNavigatesToTheLoginScreen() {
        runBlocking(koinApplication.koin.get<CoroutineDispatcherProvider>().main) {
            with(compose) {
                mainClock.autoAdvance = false
                setContent {
                    MobileApp({
                        rootComponent
                    }, koinApplication)
                }
                awaitIdle()
                mainClock.advanceTimeBy(5000)
                onNodeWithText("Login to Slack").assertIsDisplayed()
            }
        }
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        stopKoin()
    }
}