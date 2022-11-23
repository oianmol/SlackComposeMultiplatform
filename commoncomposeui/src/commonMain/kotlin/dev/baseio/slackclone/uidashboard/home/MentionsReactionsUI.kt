package dev.baseio.slackclone.uidashboard.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@Composable
internal fun MentionsReactionsUI() {
    SlackCloneSurface(color = LocalSlackCloneColor.current.uiBackground, modifier = Modifier.fillMaxSize()) {
        Column {
            MRTopAppBar()
        }
    }
}

@Composable
internal fun MRTopAppBar() {
    SlackSurfaceAppBar(
        title = {
            Text(text = "Mentions & Reactions", style = SlackCloneTypography.h5.copy(color = Color.White, fontWeight = FontWeight.Bold))
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor
    )
}
