package dev.baseio.slackserver.services

import database.SkUser
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.UsersDataSource
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class UserService(coroutineContext: CoroutineContext = Dispatchers.IO, private val usersDataSource: UsersDataSource) :
  UsersServiceGrpcKt.UsersServiceCoroutineImplBase(coroutineContext) {

  override suspend fun currentLoggedInUser(request: Empty): SKUser {
    val authData = AUTH_CONTEXT_KEY.get() ?: throw StatusException(Status.UNAUTHENTICATED)
    return usersDataSource.getUser(authData.userId, authData.workspaceId)?.toGrpc()
      ?: throw StatusException(Status.UNAUTHENTICATED)
  }

  override fun getUsers(request: SKWorkspaceChannelRequest): Flow<SKUsers> {
    val authData = AUTH_CONTEXT_KEY.get() ?: throw StatusException(Status.UNAUTHENTICATED)
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
    this.uuid.takeIf { !it.isNullOrEmpty() } ?: userId,
    this.workspaceId,
    this.gender,
    this.name.takeIf { !it.isNullOrEmpty() } ?: this.email.split("@").first(),
    this.location,
    this.email,
    this.username.takeIf { !it.isNullOrEmpty() } ?: this.email.split("@").first(),
    this.userSince.toInt(),
    this.phone,
    this.avatarUrl.takeIf { !it.isNullOrEmpty() } ?: "https://picsum.photos/300/300"
  )
}
