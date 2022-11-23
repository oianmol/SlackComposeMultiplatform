package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SlackDesktopLayout(
    modifier: Modifier = Modifier,
    sideBar: @Composable (Modifier) -> Unit,
    workSpaceAndChannels: @Composable (Modifier) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Row(modifier) {
        sideBar(Modifier.width(80.dp).fillMaxHeight())
        workSpaceAndChannels(Modifier.weight(1f).fillMaxHeight())
        content(Modifier.weight(3f).fillMaxHeight())
    }
}
