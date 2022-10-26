import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.android.MobileApp
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinApplication
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class AuthCreateWorkspaceUITest {
    @get:Rule
    val compose = createComposeRule()

    private val lifecycle = LifecycleRegistry()
    private val skKeyValueData = SKKeyValueData(ApplicationProvider.getApplicationContext())
    private val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
    lateinit var koinApplication:KoinApplication

    @Before
    fun prepare() {
        koinApplication =  initKoin({ skKeyValueData },
            { DriverFactory(ApplicationProvider.getApplicationContext()).createDriver(SlackDB.Schema) })
    }

    @Test
    fun createWorkspaceWhenCredentialsAreValid() {
        runBlocking(Dispatchers.Main) {
            compose.apply {
                mainClock.autoAdvance = false
                setContent {
                    MobileApp({
                        rootComponent
                    }, koinApplication)
                }
                awaitIdle()
                mainClock.advanceTimeBy(5000)
                onNodeWithTag("createWorkspaceButton").performClick()
            }
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }
}