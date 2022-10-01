package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import dev.baseio.slackdomain.usecases.channels.*
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.dsl.module

val useCaseModule = module {
  single { UseCaseFetchRecentChannels(get()) }
  single { UseCaseGetSelectedWorkspace(get()) }
  single { UseCaseFetchChannels(get()) }
  single { UseCaseFetchChannelsWithLastMessage(get()) }
  single { UseCaseFetchMessages(get()) }
  single { UseCaseSendMessage(get()) }
  single { UseCaseCreateChannel(get()) }
  single { UseCaseCreateOneToOneChannel(get()) }
  single { UseCaseGetChannel(get()) }
  single { UseCaseFetchChannelCount(get()) }
  single { UseCaseSearchChannel(get()) }
  single { UseCaseFetchUsers(get()) }
}