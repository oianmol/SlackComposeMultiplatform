import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.onboarding.vmtest.SlackKoinTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class AppUiTestSetup : SlackKoinTest(), UiTestDiSetup {

    @get:Rule
    override val rule = createAndroidComposeRule<ComponentActivity>()

    private val rootComponent by lazy {
        RootComponent(
            DefaultComponentContext(
                LifecycleRegistry()
            )
        )
    }


    @Before
    override fun setupKoin() {
        koinApplication.koin.also {
            it.loadModules(listOf(module {
                single<Context> { rule.activity }
            }))
        }
    }

    context(ComposeContentTestRule)
    override fun setAppContent() {
        setContent {
            SlackApp {
                rootComponent
            }
        }
    }

    @After
    override fun close() {
        stopKoin()
    }
}