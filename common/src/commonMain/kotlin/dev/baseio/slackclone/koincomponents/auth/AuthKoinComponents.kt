package dev.baseio.slackclone.koincomponents.auth

import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.channels.*
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.baseio.slackdomain.usecases.workspaces.UseCaseAuthWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AuthKoinComponents : KoinComponent {
    fun provideUseCaseCreateWorkspace(): UseCaseAuthWorkspace = get()
    fun provideUseCaseCurrentUser(): UseCaseFetchAndSaveCurrentUser = get()
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
}