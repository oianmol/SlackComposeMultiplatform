package dev.baseio.slackclone.commonui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
fun SlackImageBox(modifier: Modifier, imageUrl: String) {
  AsyncImage(model = imageUrl, modifier = modifier, contentDescription = null)
}