package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.chatcore.injection.SlackChannelUiLayerChannels
import dev.baseio.slackclone.navigation.BackstackScreen
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackclone.uichannels.createsearch.CreateChannelVM
import dev.baseio.slackclone.uichannels.createsearch.SearchChannelsVM
import dev.baseio.slackclone.uichannels.directmessages.MessageViewModel
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import dev.baseio.slackclone.uichat.newchat.NewChatThreadVM
import dev.baseio.slackclone.uidashboard.compose.DashboardVM
import dev.baseio.slackclone.uidashboard.compose.SideNavVM
import dev.baseio.slackclone.uidashboard.home.HomeScreenVM
import dev.baseio.slackclone.uionboarding.GettingStartedVM
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchRecentChannels
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getFullName

val viewModelModule = module {
  scope<BackstackScreen> {
    scoped {
      HomeScreenVM(getKoin().get())
    }
    scoped { GettingStartedVM() }
    scoped { SideNavVM(getKoin().get()) }
    scoped { DashboardVM() }
    scoped {
      ChatScreenVM(getKoin().get(), getKoin().get())
    }
    scoped {
      CreateChannelVM(getKoin().get(), getKoin().get(), getKoin().get(SlackChannelUiLayerChannels))
    }
    scoped {
      NewChatThreadVM(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get())
    }
    scoped {
      MessageViewModel(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get())
    }
    scoped {
      SearchChannelsVM(getKoin().get(), getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get())
    }
    scoped(qualifier = RecentChatsQualifier) { SlackChannelVM(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get(), getKoin().get()) }
    scoped(qualifier = StarredChatsQualifier) { SlackChannelVM(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get(), getKoin().get()) }
    scoped(qualifier = DirectChatsQualifier) { SlackChannelVM(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get(), getKoin().get()) }
    scoped(qualifier = AllChatsQualifier) { SlackChannelVM(getKoin().get(), getKoin().get(SlackChannelUiLayerChannels), getKoin().get(), getKoin().get()) }
  }
}


object RecentChatsQualifier : Qualifier{
  override val value: QualifierValue
    get() = "RecentChatsQualifier"
}

object StarredChatsQualifier : Qualifier{
  override val value: QualifierValue
    get() = "StarredChatsQualifier"
}

object DirectChatsQualifier : Qualifier{
  override val value: QualifierValue
    get() = "DirectChatsQualifier"
}

object AllChatsQualifier : Qualifier{
  override val value: QualifierValue
    get() = "AllChatsQualifier"
}