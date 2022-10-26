package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.local.SKLocalDatabaseSourceImpl
import dev.baseio.slackdata.datasources.local.SKLocalKeyValueSourceImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceChannelMembersImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceCreateChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SlackSKLocalDataSourceChannelLastMessage
import dev.baseio.slackdata.datasources.local.messages.SKLocalDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.local.users.SKLocalDataSourceCreateUsersImpl
import dev.baseio.slackdata.datasources.local.users.SKLocalDataSourceUsersImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKLocalDataSourceWriteWorkspacesImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKLocalDataSourceReadWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.auth.SKAuthNetworkDataSourceImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceReadChannelMembersImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceWriteChannelsImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkSourceChannelImpl
import dev.baseio.slackdata.datasources.remote.messages.SKNetworkDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.remote.users.SKNetworkDataSourceReadUsersImpl
import dev.baseio.slackdata.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.workspaces.SKNetworkDataSourceWriteWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.workspaces.SKNetworkSourceWorkspacesImpl
import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceWriteUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannelMembers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkSourceWorkspaces
import org.koin.dsl.module

val dataSourceModule = module {
  single<SKLocalDatabaseSource> {
    SKLocalDatabaseSourceImpl(get())
  }
  single<SKLocalKeyValueSource> {
    SKLocalKeyValueSourceImpl(get())
  }
  single<SKNetworkSourceChannel> {
    SKNetworkSourceChannelImpl(get())
  }
  single<SKNetworkSourceWorkspaces> {
    SKNetworkSourceWorkspacesImpl(get())
  }
  single<SKAuthNetworkDataSource> {
    SKAuthNetworkDataSourceImpl(get())
  }
  single<SKLocalDataSourceChannelMembers> {
    SKLocalDataSourceChannelMembersImpl(get(), get())
  }
  single<SKNetworkDataSourceReadChannelMembers> {
    SKNetworkDataSourceReadChannelMembersImpl(get())
  }
  single<SKNetworkDataSourceMessages> {
    SKNetworkDataSourceMessagesImpl(get())
  }
  single<SKNetworkDataSourceReadUsers> {
    SKNetworkDataSourceReadUsersImpl(get(), get())
  }
  single<SKNetworkDataSourceReadWorkspaces> { SKNetworkDataSourceReadWorkspacesImpl(get()) }
  single<SKNetworkDataSourceWriteWorkspaces> {
    SKNetworkDataSourceWriteWorkspacesImpl(get())
  }
  single<SKLocalDataSourceWriteUsers> {
    SKLocalDataSourceCreateUsersImpl(get(), get())
  }
  single<SKLocalDataSourceWriteWorkspaces> {
    SKLocalDataSourceWriteWorkspacesImpl(get(), get())
  }
  single<SKLocalDataSourceReadWorkspaces> {
    SKLocalDataSourceReadWorkspacesImpl(get(), get(SlackWorkspaceMapperQualifier), get())
  }
  single<SKNetworkDataSourceWriteChannels> {
    SKNetworkDataSourceWriteChannelsImpl(get(), get())
  }
  single<SKNetworkDataSourceReadChannels> {
    SKNetworkDataSourceReadChannelsImpl(get(), get())
  }
  single<SKLocalDataSourceCreateChannels> {
    SKLocalDataSourceCreateChannelsImpl(
      get(),
      get(SlackChannelDMChannelQualifier),
      get(qualifier = SlackChannelChannelQualifier),
      get(),
    )
  }
  single<SKLocalDataSourceReadChannels> {
    SKLocalDataSourceReadChannelsImpl(
      get(),
      get(SlackChannelChannelQualifier),
      get(SlackChannelDMChannelQualifier),
      get(), get(), get()
    )
  }
  single<SKLocalDataSourceUsers> { SKLocalDataSourceUsersImpl(get(), get(SlackUserRandomUserQualifier)) }
  single<SKLocalDataSourceMessages> {
    SKLocalDataSourceMessagesImpl(
      get(),
      get(SlackMessageMessageQualifier),
      get()
    )
  }
  single<SKLocalDataSourceChannelLastMessage> {
    SlackSKLocalDataSourceChannelLastMessage(
      get(),
      get(),
      get(SlackMessageMessageQualifier),
      get(SlackChannelChannelQualifier),
      get(SlackChannelDMChannelQualifier),
      get(),
      get()
    )
  }
}

