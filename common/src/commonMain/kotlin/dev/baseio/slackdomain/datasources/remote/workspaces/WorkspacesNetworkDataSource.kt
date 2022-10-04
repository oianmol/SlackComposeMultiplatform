package dev.baseio.slackdomain.datasources.remote.workspaces

import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.Email
import dev.baseio.slackdomain.usecases.workspaces.Name

interface WorkspacesNetworkDataSource {
  suspend fun findWorkspacesForEmail(email: Email): KMSKWorkspaces
  suspend fun findWorkspaceByName(name: Name): KMSKWorkspace
}