package dev.baseio.slackdomain.datasources.remote.workspaces

interface SKNetworkSourceWorkspaces {
    suspend fun sendMagicLink(email: String, domain: String)
}
