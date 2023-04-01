package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkDataSourceReadWorkspaces

class UseCaseFetchAndSaveWorkspaces(
    private val skLocalKeyValueSource: SKLocalKeyValueSource,
    private val skNetworkDataSourceReadWorkspaces: SKNetworkDataSourceReadWorkspaces,
    private val skLocalDataSourceWriteWorkspaces: SKLocalDataSourceWriteWorkspaces,
    private val setLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace
) {
    suspend operator fun invoke(token: String) {
        kotlin.runCatching {
            skLocalKeyValueSource.save(AUTH_TOKEN, token)
            val kmSKWorkspaces = skNetworkDataSourceReadWorkspaces.getWorkspaces(token)
            // TODO there will be always one workspace change list<Item> to Item in grpc call.
            val workspaces = kmSKWorkspaces.map { skWorkspace -> skWorkspace.copy(token = token) }
            skLocalDataSourceWriteWorkspaces.saveWorkspaces(workspaces)
            setLastSelectedWorkspace.invoke(workspaces.first())
        }
    }
}
