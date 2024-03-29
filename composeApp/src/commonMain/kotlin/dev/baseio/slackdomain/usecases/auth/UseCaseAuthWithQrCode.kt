package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces

class UseCaseAuthWithQrCode(
    private val skLocalDatabaseSource: SKLocalDatabaseSource,
    private val useCaseFetchAndSaveWorkspaces: UseCaseFetchAndSaveWorkspaces,
) {
    suspend operator fun invoke(result: DomainLayerUsers.SKAuthResult) {
        skLocalDatabaseSource.clear()
        useCaseFetchAndSaveWorkspaces.invoke(result.token)
    }
}
