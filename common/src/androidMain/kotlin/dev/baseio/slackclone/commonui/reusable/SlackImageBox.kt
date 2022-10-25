package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
actual fun SlackImageBox(modifier: Modifier, imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        modifier = modifier.clip(RoundedCornerShape(25)),
        contentDescription = null,
        contentScale = ContentScale.FillBounds
    )
}
