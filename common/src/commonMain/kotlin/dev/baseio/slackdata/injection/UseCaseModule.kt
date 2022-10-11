package dev.baseio.slackdata.injection

import dev.baseio.slackdomain.usecases.auth.LoginUseCase
import dev.baseio.slackdomain.usecases.auth.UseCaseClearAuth
import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import dev.baseio.slackdomain.usecases.auth.UseCaseRegisterUser
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import dev.baseio.slackdomain.usecases.channels.*
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchMessages
import dev.baseio.slackdomain.usecases.users.UseCaseFetchLocalUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.*
import org.koin.dsl.module

val useCaseModule = module {
    single { LoginUseCase(get(), get()) }
    single { FindWorkspacesUseCase(get()) }
    single { UseCaseFetchWorkspaces(get(), get(), get()) }
    single { UseCaseSetLastSelectedWorkspace(get()) }
    single { UseCaseFetchRecentChannels(get()) }
    single { UseCaseGetSelectedWorkspace(get()) }
    single { UseCaseFetchChannels(get(), get(), get()) }
    single { UseCaseFetchChannelsWithLastMessage(get()) }
    single { UseCaseFetchMessages(get(), get(), get()) }
    single { UseCaseSendMessage(get(), get()) }
    single { UseCaseCreateChannel(get(), get()) }
    single { UseCaseCreateOneToOneChannel(get()) }
    single { UseCaseGetChannel(get()) }
    single { UseCaseFetchChannelCount(get()) }
    single { UseCaseSearchChannel(get()) }
    single { UseCaseFetchLocalUsers(get()) }
    single { UseCaseFindChannelById(get(), get(), get()) }
    single { UseCaseFetchAndSaveUsers(get(), get(), get()) }
    single { UseCaseClearAuth(get()) }
    single { UseCaseCurrentUser(get()) }
    single { UseCaseCreateWorkspace(get(), get(), get(),get()) }
    single { UseCaseRegisterUser(get(),get(),get()) }
}