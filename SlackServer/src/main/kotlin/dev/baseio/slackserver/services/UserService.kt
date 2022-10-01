package dev.baseio.slackserver.services

import database.SkUser
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.UsersDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class UserService(coroutineContext: CoroutineContext = Dispatchers.IO, private val usersDataSource: UsersDataSource) :
  UsersServiceGrpcKt.UsersServiceCoroutineImplBase(coroutineContext) {
  override fun getUsers(request: SKWorkspaceChannelRequest): Flow<SKUsers> {
    return usersDataSource.getUsers(request.workspaceId).map {
      val users = it.executeAsList().map { user ->
        user.toGrpc()
      }
      SKUsers.newBuilder()
        .addAllUsers(users)
        .build()
    }
  }

  override suspend fun saveUser(request: SKUser): SKUser {
    return usersDataSource
      .saveUser(request.toDBUser())
      .toGrpc()
  }


}

fun SkUser.toGrpc(): SKUser {
  return SKUser.newBuilder()
    .setUuid(this.uuid)
    .setWorkspaceId(this.workspaceId)
    .setPhone(this.phone)
    .setAvatarUrl(this.avatarUrl)
    .setGender(this.gender)
    .setName(this.name)
    .setUserSince(this.userSince.toLong())
    .setUsername(this.username)
    .setEmail(this.email)
    .setLocation(this.location)
    .build()
}

fun SKUser.toDBUser(userId: String = UUID.randomUUID().toString()): SkUser {
  return SkUser(
    this.uuid ?: userId,
    this.workspaceId,
    this.gender,
    this.name,
    this.location,
    this.email,
    this.username,
    this.userSince.toInt(),
    this.phone,
    this.avatarUrl
  )
}
