import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

actual object PainterRes {
    @Composable
    actual fun gettingStarted(): Painter {
        return rememberVectorPainter(Icons.Default.Home)
    }

    @Composable
    actual fun slackLogo(): Painter {
        return rememberVectorPainter(Icons.Default.Home)
    }
}
