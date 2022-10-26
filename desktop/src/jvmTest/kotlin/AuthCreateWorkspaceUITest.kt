import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test

class AuthCreateWorkspaceUITest {
    @get:Rule
    val compose = createComposeRule()

    private val lifecycle = LifecycleRegistry()
    private val skKeyValueData = SKKeyValueData()
    private val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }

    @Test
    fun `create workspace when credentials are valid`() {
        runBlocking(Dispatchers.Main) {
            compose.apply {
                mainClock.autoAdvance = false
                setContent {

                }
                awaitIdle()
                mainClock.advanceTimeBy(5000)
                onNodeWithTag("createWorkspaceButton").performClick()
            }
        }
    }
    @After
    fun teardown(){

    }
}