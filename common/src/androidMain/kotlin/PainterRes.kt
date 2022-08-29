import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import org.example.common.R

actual object PainterRes {
  @Composable
  actual fun gettingStarted() = painterResource(R.drawable.gettingstarted)

  @Composable
  actual fun homeTabIcon() = painterResource(R.drawable.ic_home)
}