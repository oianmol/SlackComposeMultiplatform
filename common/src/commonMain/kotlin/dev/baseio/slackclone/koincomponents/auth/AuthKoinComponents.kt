package dev.baseio.slackclone.koincomponents.auth

import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAllChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AuthKoinComponents : KoinComponent {
    fun provideUseCaseCreateWorkspace(): UseCaseCreateWorkspace = get()
    fun provideUseCaseCurrentUser(): UseCaseCurrentUser = get()
    fun providerUseCaseGetSelectedWorkspace(): UseCaseGetSelectedWorkspace = get()
    fun providerUseCaseFetchChannelsWithLastMessage(): UseCaseFetchChannelsWithLastMessage = get()
    fun providerUseCaseFetchAndSaveChannels(): UseCaseFetchAndSaveChannels = get()

    fun provideUseCaseGetMessages(): UseCaseStreamLocalMessages = get()
    fun provideUseCaseSendMessage(): UseCaseSendMessage = get()
    fun provideUseCaseFetchAndSaveWorkspaces(): UseCaseFetchAndSaveWorkspaces = get()
    fun provideUseCaseCreateChannel(): UseCaseCreateChannel = get()
    fun provideUseCaseFetchAllChannels(): UseCaseFetchAllChannels = get()
    fun localUser(): DomainLayerUsers.SKUser = get<SKLocalKeyValueSource>().skUser()
}