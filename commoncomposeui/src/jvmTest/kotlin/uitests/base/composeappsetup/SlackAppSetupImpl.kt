package uitests.base.composeappsetup

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.slackKoinApp
import dev.baseio.slackdomain.usecases.auth.UseCaseLogout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class SlackAppSetupImpl : SlackKoinTest(), SlackAppSetup {

    @get:Rule
    override val rule = createComposeRule()

    override val rootComponent by lazy {
        RootComponent(
            DefaultComponentContext(
                LifecycleRegistry()
            )
        )
    }

    @Before
    override fun setupKoin(): Unit = runBlocking {
        testKoinApplication.also {
            slackKoinApp = it
            slackKoinApp.koin.get<UseCaseLogout>().invoke()
        }
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