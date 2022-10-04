package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.local.channels.SKDataSourceChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SKDataSourceCreateChannelsImpl
import dev.baseio.slackdata.datasources.local.channels.SlackSKDataSourceChannelLastMessage
import dev.baseio.slackdata.datasources.local.messages.SlackSKDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.local.users.SKDataSourceCreateUsersImpl
import dev.baseio.slackdata.datasources.local.users.SKDataSourceUsersImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKDataSourceCreateWorkspacesImpl
import dev.baseio.slackdata.datasources.local.workspaces.SKDataSourceWorkspacesImpl
import dev.baseio.slackdata.datasources.remote.auth.AuthNetworkDataSourceImpl
import dev.baseio.slackdata.datasources.remote.workspaces.WorkspacesNetworkDataSourceImpl
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannels
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.messages.SKDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceUsers
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceCreateWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.datasources.remote.auth.AuthNetworkDataSource
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
  single<SKDataSourceCreateWorkspaces> {
    SKDataSourceCreateWorkspacesImpl(get(), get())
  }
  single<SKDataSourceWorkspaces> {
    SKDataSourceWorkspacesImpl(get(), get(SlackWorkspaceMapperQualifier), get())
  }
  single<SKDataSourceCreateChannels> {
    SKDataSourceCreateChannelsImpl(
      get(),
      get(qualifier = SlackChannelChannelQualifier),
      get()
    )
  }
  single<SKDataSourceChannels> {
    SKDataSourceChannelsImpl(get(), get(SlackChannelChannelQualifier), get())
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

