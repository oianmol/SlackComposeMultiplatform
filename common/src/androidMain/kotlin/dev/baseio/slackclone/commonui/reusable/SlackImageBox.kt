package dev.baseio.slackclone.commonui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
actual fun SlackImageBox(modifier: Modifier, imageUrl: String) {
  AsyncImage(model = imageUrl, modifier = modifier, contentDescription = null, contentScale = ContentScale.FillBounds)
}