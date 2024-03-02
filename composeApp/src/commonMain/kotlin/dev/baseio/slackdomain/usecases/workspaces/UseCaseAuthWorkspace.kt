package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkSourceWorkspaces
import dev.baseio.slackdomain.isEmailValid

class UseCaseAuthWorkspace(
    private val workspaceSource: SKNetworkSourceWorkspaces,
) {
    suspend operator fun invoke(email: String, domain: String) {
        if (isEmailValid(email)) {
            return workspaceSource.sendMagicLink(email, domain)
        } else {
            throw Exception("Email is invalid.")
        }
    }
}
