import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.KoinApplication

class AuthCreateWorkspaceUITest {
    @get:Rule
    val compose = createComposeRule()

    private val lifecycle = LifecycleRegistry()
    private val skKeyValueData = SKKeyValueData()
    private val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }

    lateinit var koinApplication: KoinApplication

    @Before
    fun prepare() {
        koinApplication =  initKoin({ skKeyValueData },
            { DriverFactory().createDriver(SlackDB.Schema) })
    }


    @Test
    fun `create workspace when credentials are valid`() {
        compose.apply {
            mainClock.autoAdvance = false
            setContent {
                var rememberedComposeWindow by remember {
                    mutableStateOf(WindowInfo(1024.dp,768.dp))
                }
                DesktopApp(rememberedComposeWindow,{
                    rootComponent
                }, koinApplication)
            }
            mainClock.advanceTimeBy(5000)
            onNodeWithTag("createWorkspaceButton").performClick()
        }
    }
    @After
    fun teardown(){

    }
}