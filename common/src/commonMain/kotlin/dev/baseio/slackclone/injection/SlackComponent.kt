package dev.baseio.slackclone.injection

import dev.baseio.slackclone.data.injection.RecentChatsQualifier
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.Qualifier

class SlackComponent : KoinComponent {
  fun provideSideNavVM(): SideNavVM = get()
  fun provideGettingStartedVM(): GettingStartedVM = get()
  fun provideDashboardVM(): DashboardVM = get()
  fun provideChatScreenVM(): ChatScreenVM = get()
  fun provideCreateChannelVM(): CreateChannelVM = get()
  fun provideNewChatThreadVM(): NewChatThreadVM = get()
  fun provideSlackChannelVM(qualifier: Qualifier): SlackChannelVM = get(qualifier)
  fun provideMessageViewModel(): MessageViewModel = get()
  fun provideSearchChannelsVM(): SearchChannelsVM = get()
  fun provideHomeScreenVM(): HomeScreenVM = get()
}