package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun SlackImageBox(modifier: Modifier, imageUrl: String) {
  Icon(
    imageVector = Icons.Default.Email,
    modifier = modifier.clip(RoundedCornerShape(25)),
    contentDescription = null,
  )
}