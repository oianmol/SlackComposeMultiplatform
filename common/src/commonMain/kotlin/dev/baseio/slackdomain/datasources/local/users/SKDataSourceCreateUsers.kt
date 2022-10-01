package dev.baseio.slackdomain.datasources.local.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKDataSourceCreateUsers {
  suspend fun saveUsers(users:List<DomainLayerUsers.SKUser>)
}