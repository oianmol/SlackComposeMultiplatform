package dev.baseio.slackdomain.datasources.local.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKLocalDataSourceWriteUsers {
  suspend fun saveUsers(users:List<DomainLayerUsers.SKUser>)
}