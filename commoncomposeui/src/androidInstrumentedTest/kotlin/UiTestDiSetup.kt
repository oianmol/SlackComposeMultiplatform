import androidx.compose.ui.test.junit4.ComposeContentTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule

interface UiTestDiSetup {

    @get:Rule
    val rule: ComposeContentTestRule
    @Before
    fun setupKoin()

    context(ComposeContentTestRule)
    fun setAppContent()

    @After
    fun close()
}