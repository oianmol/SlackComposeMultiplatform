package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceCreateChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SlackSKDataSourceChannelLastMessage
import dev.baseio.slackdata.datasources.local.messages.SlackSKDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.local.users.SKDataSourceCreateUsersImpl
import dev.baseio.slackdata.datasources.local.users.SKDataSourceUsersImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKLocalDataSourceWriteWorkspacesImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKDataSourceWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.auth.AuthNetworkDataSourceImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceReadChannelsImpl
import dev.baseio.slackdata.datasources.remote.channels.SKNetworkDataSourceWriteChannelsImpl
import dev.baseio.slackdata.datasources.remote.workspaces.WorkspacesNetworkDataSourceImpl
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.messages.SKDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceUsers
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.datasources.remote.auth.AuthNetworkDataSource
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.datasources.remote.workspaces.WorkspacesNetworkDataSource
import org.koin.dsl.module

val dataSourceModule = module {
  single<AuthNetworkDataSource> {
    AuthNetworkDataSourceImpl(get())
  }
  single<WorkspacesNetworkDataSource> { WorkspacesNetworkDataSourceImpl(get()) }
  single<SKDataSourceCreateUsers> {
    SKDataSourceCreateUsersImpl(get(), get())
  }
  single<SKLocalDataSourceWriteWorkspaces> {
    SKLocalDataSourceWriteWorkspacesImpl(get(), get())
  }
  single<SKDataSourceWorkspaces> {
    SKDataSourceWorkspacesImpl(get(), get(SlackWorkspaceMapperQualifier), get())
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
  single<SKDataSourceUsers> { SKDataSourceUsersImpl(get(), get(SlackUserRandomUserQualifier), get()) }
  single<SKDataSourceMessages> { SlackSKDataSourceMessagesImpl(get(), get(SlackMessageMessageQualifier), get()) }
  single<SKDataSourceChannelLastMessage> {
    SlackSKDataSourceChannelLastMessage(
      get(),
      get(SlackMessageMessageQualifier),
      get(SlackChannelChannelQualifier),
      get()
    )
  }
}

