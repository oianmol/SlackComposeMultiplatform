package dev.baseio.slackclone.koincomponents.auth

import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import dev.baseio.slackdomain.usecases.channels.*
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
    fun provideUseCaseFetchAndSaveChannelMembers(): UseCaseFetchAndSaveChannelMembers = get()
    fun provideUseCaseGetMessages(): UseCaseStreamLocalMessages = get()
    fun provideIMessageDecrypter(): IMessageDecrypter = get()
    fun provideUseCaseSendMessage(): UseCaseSendMessage = get()
    fun provideUseCaseFetchAndSaveWorkspaces(): UseCaseFetchAndSaveWorkspaces = get()
    fun provideUseCaseCreateChannel(): UseCaseCreateChannel = get()
    fun provideUseCaseFetchAllChannels(): UseCaseFetchAllChannels = get()
    fun localUser(): DomainLayerUsers.SKUser = get<SKLocalKeyValueSource>().skUser()
}