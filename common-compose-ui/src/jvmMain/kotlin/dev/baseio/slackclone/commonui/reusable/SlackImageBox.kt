package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
internal actual fun SlackImageBox(modifier: Modifier, imageUrl: String) {
    KamelImage(
        resource = lazyPainterResource(
            data = imageUrl
        ),
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(25)),
        contentScale = ContentScale.FillBounds
    )
}
