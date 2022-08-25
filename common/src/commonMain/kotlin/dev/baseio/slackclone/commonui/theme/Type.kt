package dev.baseio.slackclone.commonui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SlackCloneTypography = Typography(
  body1 = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
  ),
  button = TextStyle(
    fontWeight = FontWeight.W500,
    fontSize = 14.sp
  ),
  caption = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
  )
)