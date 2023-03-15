package dev.baseio.slackdomain.datasources.remote.workspaces

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKNetworkSourceWorkspaces {
  suspend fun sendMagicLink(email:String, domain: String)
}