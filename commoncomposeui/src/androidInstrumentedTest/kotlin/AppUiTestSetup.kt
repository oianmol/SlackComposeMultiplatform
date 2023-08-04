import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class AppUiTestSetup : SlackKoinTest(), UiTestDiSetup {

    @get:Rule
    override val rule = createAndroidComposeRule<ComponentActivity>()

    override val rootComponent by lazy {
        RootComponent(
            DefaultComponentContext(
                LifecycleRegistry()
            )
        )
    }


    @Before
    override fun setupKoin() : Unit = runBlocking {
        koinApplication.koin.also {
            it.loadModules(listOf(module {
                single<Context> { rule.activity }
            }))
        }
        setupMocks()
    }

    context(ComposeContentTestRule)
    override fun setAppContent() {
        setContent {
            SlackCloneTheme {
                SlackApp {
                    rootComponent
                }
            }
        }
    }

    @After
    override fun close() {
        stopKoin()
    }
}