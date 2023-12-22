package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackdomain.model.users.DomainLayerUsers

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ChannelMembersDialog(
    members: List<DomainLayerUsers.SKUser>,
    close: () -> Unit
) {
    Column {
        ListItem(
            text = {
                Text("Channel Members")
            },
            trailing = {
                IconButton(onClick = {
                    close()
                }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            },
            modifier = Modifier.background(
                LocalSlackCloneColor.current.appBarColor,
                shape = RoundedCornerShape(12.dp)
            )
        )

        LazyColumn(Modifier) {
            items(members) { skUser ->
                SlackListItem(icon = Icons.Default.Person, title = skUser.name ?: "--")
            }
        }
    }
}