package dev.baseio.slackdomain.datasources.remote.auth

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKAuthNetworkDataSource {
    suspend fun getLoggedInUser(): Result<DomainLayerUsers.SKUser>
}