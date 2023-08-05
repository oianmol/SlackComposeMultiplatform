import androidx.compose.ui.test.junit4.ComposeContentTestRule
import dev.baseio.slackclone.RootComponent
import org.junit.After
import org.junit.Before
import org.junit.Rule

interface SlackAppSetup {

    @get:Rule
    val rule: ComposeContentTestRule
    val rootComponent: RootComponent

    @Before
    fun setupKoin()

    context(ComposeContentTestRule)
    fun setAppContent()

    @After
    fun close()
}