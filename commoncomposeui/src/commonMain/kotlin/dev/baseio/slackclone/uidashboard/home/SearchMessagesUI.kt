package dev.baseio.slackclone.uidashboard.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.uidashboard.home.search.SearchCancel

@Composable
internal fun SearchMessagesUI() {
    SlackCloneSurface(
        color = LocalSlackCloneColor.current.uiBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            SearchTopAppBarMessages()
            Content()
        }
    }
}

@Composable
internal fun SearchTopAppBarMessages() {
    SlackSurfaceAppBar(
        backgroundColor = LocalSlackCloneColor.current.appBarColor,
        contentPadding = PaddingValues(8.dp)
    ) {
        SearchCancel()
    }
}

@Composable
internal fun Content() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        SlackListItem(
            icon = Icons.Default.ShoppingCart,
            title = "browse_people"
        )
        SlackListItem(icon = Icons.Default.Search, title = "browse_channels")
        SlackListDivider()
        // Recent Searches
        SearchText("recent_searches")
        repeat(5) {
            SlackListItem(
                icon = Icons.Default.Favorite,
                title = "in:#android_india",
                trailingItem = Icons.Default.Clear
            )
        }
        SlackListDivider()
        // Narrow Your Search
        SearchText("narrow_your_search")
        repeat(5) {
            SlackListItemTrailingView(
                icon = Icons.Default.Favorite,
                title = "from:",
                trailingView = {
                    Text(text = "Ex: @zoemaxwell")
                }
            )
        }
    }
}

@Composable
internal fun SearchText(title: String) {
    Text(
        text = title,
        style = SlackCloneTypography.caption.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
internal fun SlackListDivider() {
    Divider(color = LocalSlackCloneColor.current.lineColor, thickness = 0.5.dp)
}
