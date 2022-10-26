package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.usecases.auth.*
import dev.baseio.slackdomain.usecases.channels.*
import dev.baseio.slackdomain.usecases.chat.*
import dev.baseio.slackdomain.usecases.users.UseCaseFetchLocalUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndUpdateChangeInUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.*
import org.koin.dsl.module

val useCaseModule = module {
  factory { LoginUseCase(get(), get(), get()) }
  factory { UseCaseGetChannelMembers(get(), get()) }
  factory { FindWorkspacesUseCase(get()) }
  factory { UseCaseFetchAndSaveWorkspaces(get(), get()) }
  factory { UseCaseFetchAndSaveChannelMembers(get(), get()) }
  factory { UseCaseGetWorkspaces(get()) }
  factory { UseCaseSetLastSelectedWorkspace(get()) }
  factory { UseCaseFetchRecentChannels(get()) }
  factory { UseCaseGetSelectedWorkspace(get()) }
  factory { UseCaseFetchAndSaveChannels(get(), get()) }
  factory { UseCaseFetchChannelsWithLastMessage(get()) }
  factory { UseCaseFetchAndUpdateChangeInMessages(get(), get()) }
  factory { UseCaseFetchAndUpdateChangeInUsers(get(), get()) }
  factory { UseCaseFetchAndUpdateChangeInChannels(get(), get()) }
  factory { UseCaseFetchAndSaveMessages(get(), get()) }
  factory { UseCaseSendMessage(get(), get()) }
  factory { UseCaseStreamLocalMessages(get()) }
  factory { UseCaseRegisterUser(get(), get()) }
  factory { UseCaseFetchAllChannels(get()) }
  factory { UseCaseCreateChannel(get(), get(), get()) }
  factory { UseCaseGetChannel(get()) }
  factory { UseCaseFetchChannelCount(get()) }
  factory { UseCaseInviteUserToChannel(get()) }
  factory { UseCaseSearchChannel(get()) }
  factory { UseCaseFetchLocalUsers(get()) }
  factory { UseCaseFindChannelById(get(), get(), get()) }
  factory { UseCaseFetchAndSaveUsers(get(), get()) }
  factory { UseCaseLogout(get(), get()) }
  factory { UseCaseFetchChannelsWithSearch(get(), get(), get(), get()) }
  factory { UseCaseCurrentUser(get()) }
  factory { UseCaseCreateWorkspace(get(), get(), get(), get()) }
}