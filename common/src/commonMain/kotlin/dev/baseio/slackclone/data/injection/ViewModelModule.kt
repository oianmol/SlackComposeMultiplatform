package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.chatcore.injection.SlackChannelUiLayerChannels
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackclone.uichannels.createsearch.CreateChannelVM
import dev.baseio.slackclone.uichannels.createsearch.SearchChannelsVM
import dev.baseio.slackclone.uichannels.directmessages.MessageViewModel
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import dev.baseio.slackclone.uichat.newchat.NewChatThreadVM
import dev.baseio.slackclone.uidashboard.compose.DashboardVM
import org.koin.dsl.module

val viewModelModule = module {
  single { DashboardVM(get(), get(), get(), get(qualifier = SlackChannelUiLayerChannels)) }
  single { ChatScreenVM(get(), get()) }
  single { CreateChannelVM(get()) }
  single { NewChatThreadVM(get(), get(SlackChannelUiLayerChannels)) }
  single { SlackChannelVM(get(), get(SlackChannelUiLayerChannels)) }
  single { MessageViewModel(get(), get(SlackChannelUiLayerChannels)) }
  single { SearchChannelsVM(get(), get(), get(SlackChannelUiLayerChannels)) }
}