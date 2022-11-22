package dev.baseio.slackclone.koincomponents.auth

import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AuthKoinComponents : KoinComponent {
    fun provideUseCaseCreateWorkspace(): UseCaseCreateWorkspace = get()
}