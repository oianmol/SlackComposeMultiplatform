import androidx.compose.ui.test.junit4.createComposeRule
import com.arkivanov.decompose.DefaultComponentContext
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.onboarding.vmtest.SlackKoinUnitTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin

class AppUiTestSetup : SlackKoinUnitTest(), UiTestDiSetup {

    @get:Rule
    override val rule = createComposeRule()

    private val rootComponent by lazy {
        RootComponent(
            DefaultComponentContext(
                lifecycle = lifecycle,
                savedStateRegistry = savedStateRegistry,
                viewModelStore = viewModelStore,
                onBackPressedDispatcher = onBackPressedDispatcher
            )
        )
    }


    @Before
    override fun setupKoin() {
        koinApplication // we set this up in SlackKoinUnitTest
    }

    @After
    override fun close() {
        stopKoin()
    }
}