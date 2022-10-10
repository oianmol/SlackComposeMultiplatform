package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceCreateChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SlackSKDataSourceChannelLastMessage
import dev.baseio.slackdata.datasources.local.messages.SlackSKLocalDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.local.users.SKDataSourceCreateUsersImpl
import dev.baseio.slackdata.datasources.local.users.SKLocalDataSourceUsersImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKLocalDataSourceWriteWorkspacesImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKLocalDataSourceReadWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.auth.SKAuthNetworkDataSourceImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceWriteChannelsImpl
import dev.baseio.slackdata.datasources.remote.messages.SKNetworkDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.remote.users.SKNetworkDataSourceReadUsersImpl
import dev.baseio.slackdata.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.workspaces.SKNetworkDataSourceWriteWorkspacesImpl
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceWriteWorkspaces
import org.koin.dsl.module

val dataSourceModule = module {
  single<SKAuthNetworkDataSource> {
    SKAuthNetworkDataSourceImpl(get())
  }
  single<SKNetworkDataSourceMessages> {
    SKNetworkDataSourceMessagesImpl(get())
  }
  single<SKNetworkDataSourceReadUsers> {
    SKNetworkDataSourceReadUsersImpl(get())
  }
  single<SKNetworkDataSourceReadWorkspaces> { SKNetworkDataSourceReadWorkspacesImpl(get()) }
  single<SKNetworkDataSourceWriteWorkspaces> {
    SKNetworkDataSourceWriteWorkspacesImpl(get())
  }
  single<SKDataSourceCreateUsers> {
    SKDataSourceCreateUsersImpl(get(), get())
  }
  single<SKLocalDataSourceWriteWorkspaces> {
    SKLocalDataSourceWriteWorkspacesImpl(get(), get())
  }
  single<SKLocalDataSourceReadWorkspaces> {
    SKLocalDataSourceReadWorkspacesImpl(get(), get(SlackWorkspaceMapperQualifier), get())
  }
  single<SKNetworkDataSourceWriteChannels> {
    SKNetworkDataSourceWriteChannelsImpl(get())
  }
  single<SKNetworkDataSourceReadChannels> {
    SKNetworkDataSourceReadChannelsImpl(get())
  }
  single<SKLocalDataSourceCreateChannels> {
    SKLocalDataSourceCreateChannelsImpl(
      get(),
      get(qualifier = SlackChannelChannelQualifier),
      get()
    )
  }
  single<SKLocalDataSourceReadChannels> {
    SKLocalDataSourceReadChannelsImpl(get(), get(SlackChannelChannelQualifier), get())
  }
  single<SKLocalDataSourceUsers> { SKLocalDataSourceUsersImpl(get(), get(SlackUserRandomUserQualifier)) }
  single<SKLocalDataSourceMessages> {
    SlackSKLocalDataSourceMessagesImpl(
      get(),
      get(SlackMessageMessageQualifier),
      get()
    )
  }
  single<SKDataSourceChannelLastMessage> {
    SlackSKDataSourceChannelLastMessage(
      get(),
      get(SlackMessageMessageQualifier),
      get(SlackChannelChannelQualifier),
      get(),
      get()
    )
  }
}

