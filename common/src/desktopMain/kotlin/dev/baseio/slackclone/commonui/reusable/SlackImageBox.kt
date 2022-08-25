package dev.baseio.slackclone.commonui.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun SlackImageBox(modifier: Modifier, imageUrl: String) {
  KamelImage(
    resource = lazyPainterResource(
      data = imageUrl,
    ),
    contentDescription = null,
    modifier = modifier
  )
}