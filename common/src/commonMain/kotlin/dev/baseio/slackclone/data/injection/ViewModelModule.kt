package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.chatcore.injection.SlackChannelUiLayerChannels
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
import org.koin.dsl.module

val viewModelModule = module {
  single { HomeScreenVM(get()) }
  single { GettingStartedVM() }
  single { SideNavVM(get()) }
  single { DashboardVM() }
  single { ChatScreenVM(get(), get()) }
  single { CreateChannelVM(get(), get(), get(SlackChannelUiLayerChannels)) }
  single { NewChatThreadVM(get(), get(SlackChannelUiLayerChannels), get()) }

  single(qualifier = RecentChatsQualifier) { SlackChannelVM(get(), get(SlackChannelUiLayerChannels), get(), get()) }
  single(qualifier = StarredChatsQualifier) { SlackChannelVM(get(), get(SlackChannelUiLayerChannels), get(), get()) }
  single(qualifier = DirectChatsQualifier) { SlackChannelVM(get(), get(SlackChannelUiLayerChannels), get(), get()) }
  single(qualifier = AllChatsQualifier) { SlackChannelVM(get(), get(SlackChannelUiLayerChannels), get(), get()) }

  single { MessageViewModel(get(), get(SlackChannelUiLayerChannels), get()) }
  single { SearchChannelsVM(get(), get(), get(SlackChannelUiLayerChannels), get()) }
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