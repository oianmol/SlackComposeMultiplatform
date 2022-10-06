package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.usecases.auth.LoginUseCase
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import dev.baseio.slackdomain.usecases.channels.*
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackdomain.usecases.workspaces.FindWorkspacesUseCase
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.dsl.module

val useCaseModule = module {
  single { LoginUseCase(get(), get()) }
  single { FindWorkspacesUseCase(get()) }
  single { UseCaseFetchRecentChannels(get()) }
  single { UseCaseGetSelectedWorkspace(get()) }
  single { UseCaseFetchChannels(get(),get(),get()) }
  single { UseCaseFetchChannelsWithLastMessage(get()) }
  single { UseCaseFetchMessages(get()) }
  single { UseCaseSendMessage(get()) }
  single { UseCaseCreateChannel(get(), get()) }
  single { UseCaseCreateOneToOneChannel(get()) }
  single { UseCaseGetChannel(get()) }
  single { UseCaseFetchChannelCount(get()) }
  single { UseCaseSearchChannel(get()) }
  single { UseCaseFetchUsers(get()) }
}