package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.channels.SKDataSourceChannelsImpl
import dev.baseio.slackdata.datasources.channels.SKDataSourceCreateChannelsImpl
import dev.baseio.slackdata.datasources.channels.SlackSKDataSourceChannelLastMessage
import dev.baseio.slackdata.datasources.messages.SlackSKDataSourceMessagesImpl
import dev.baseio.slackdata.datasources.users.SKDataSourceCreateUsersImpl
import dev.baseio.slackdata.datasources.users.SKDataSourceUsersImpl
import dev.baseio.slackdata.datasources.workspaces.SKDataSourceCreateWorkspacesImpl
import dev.baseio.slackdata.datasources.workspaces.SKDataSourceWorkspacesImpl
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannels
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.messages.SKDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceUsers
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceCreateWorkspaces
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import org.koin.dsl.module

val dataSourceModule = module {
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

