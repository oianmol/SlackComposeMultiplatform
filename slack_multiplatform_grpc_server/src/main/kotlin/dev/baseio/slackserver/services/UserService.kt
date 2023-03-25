package dev.baseio.slackserver.services


import dev.baseio.slackdata.common.Empty
import dev.baseio.slackdata.common.sKByteArrayElement
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.models.SKUserPublicKey
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.sources.UsersDataSource
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
    override suspend fun updateSKUser(request: SKUser): SKUser {
        return usersDataSource.updateUser(request.toDBUser())?.toGrpc()
            ?: throw StatusException(Status.NOT_FOUND)
    }

    override suspend fun currentLoggedInUser(request: Empty): SKUser {
        val authData = AUTH_CONTEXT_KEY.get() ?: throw StatusException(Status.UNAUTHENTICATED)
        return usersDataSource.getUser(authData.userId, authData.workspaceId)?.toGrpc()
            ?: throw StatusException(Status.UNAUTHENTICATED)
    }

    override fun registerChangeInUsers(request: SKWorkspaceChannelRequest): Flow<SKUserChangeSnapshot> {
        return usersDataSource.getChangeInUserFor(request.workspaceId).map { skUser ->
            SKUserChangeSnapshot.newBuilder()
                .apply {
                    skUser.first?.toGrpc()?.let { skMessage ->
                        previous = skMessage
                    }
                    skUser.second?.toGrpc()?.let { skMessage ->
                        latest = skMessage
                    }
                }
                .build()
        }
    }

    override suspend fun getUsers(request: SKWorkspaceChannelRequest): SKUsers {
        return usersDataSource.getUsers(request.workspaceId).map { user ->
            user.toGrpc()
        }.run {
            SKUsers.newBuilder()
                .addAllUsers(this)
                .build()
        }
    }

    override suspend fun saveUser(request: SKUser): SKUser {
        return usersDataSource
            .saveUser(request.toDBUser())
            ?.toGrpc() ?: throw StatusException(Status.ABORTED)
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
        .setPublicKey(
            SlackKey.newBuilder()
                .addAllKeybytes(this.publicKey.keyBytes.map {
                    sKByteArrayElement {
                        this.byte = it.toInt()
                    }
                })
                .build()
        )
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
        this.userSince,
        this.phone,
        this.avatarUrl.takeIf { !it.isNullOrEmpty() } ?: "https://picsum.photos/300/300",
        SKUserPublicKey(keyBytes = this.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray())
    )
}
