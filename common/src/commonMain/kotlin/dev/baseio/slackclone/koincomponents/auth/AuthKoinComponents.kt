package dev.baseio.slackclone.koincomponents.auth

import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AuthKoinComponents : KoinComponent {
    fun provideUseCaseCreateWorkspace(): UseCaseCreateWorkspace = get()
    fun provideUseCaseCurrentUser(): UseCaseCurrentUser = get()
    fun providerUseCaseGetSelectedWorkspace(): UseCaseGetSelectedWorkspace = get()
    fun providerUseCaseFetchChannelsWithLastMessage(): UseCaseFetchChannelsWithLastMessage = get()
    fun provideUseCaseGetMessages(): UseCaseStreamLocalMessages = get()
}