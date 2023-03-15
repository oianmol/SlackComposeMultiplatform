package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces

class UseCaseQRAuthUser(
    private val skLocalDatabaseSource: SKLocalDatabaseSource,
    private val useCaseFetchAndSaveWorkspaces: UseCaseFetchAndSaveWorkspaces,
) {
    suspend operator fun invoke(result: DomainLayerUsers.SKAuthResult) {
        useCaseFetchAndSaveWorkspaces.invoke(result.token)
        skLocalDatabaseSource.clear()
    }
}