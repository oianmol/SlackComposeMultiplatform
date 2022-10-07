package dev.baseio.slackdomain.usecases.auth

import SKKeyValueData
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LoginUseCase(
  private val SKAuthNetworkDataSource: SKAuthNetworkDataSource,
  private val skKeyValueData: SKKeyValueData
) {
  suspend operator fun invoke(email: String, password: String, workspaceId: String) {
    val result = SKAuthNetworkDataSource.login(email, password, workspaceId).getOrThrow()
    skKeyValueData.save(AUTH_TOKEN, result.token)
    val user = SKAuthNetworkDataSource.getLoggedInUser().getOrThrow().toSKUser()
    val json = Json.encodeToString(user)
    skKeyValueData.save(LOGGED_IN_USER, json)
  }
}


fun KMSKUser.toSKUser(): DomainLayerUsers.SKUser {
  return DomainLayerUsers.SKUser(
    this.uuid,
    this.workspaceId,
    this.gender,
    this.name,
    this.location,
    this.email,
    this.username,
    this.userSince,
    this.phone,
    this.avatarUrl
  )
}
