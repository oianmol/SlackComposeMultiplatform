package dev.baseio.slackserver.services

import dev.baseio.slackdata.common.Empty
import dev.baseio.slackdata.common.empty
import dev.baseio.slackdata.protos.SKAuthResult
import dev.baseio.slackdata.protos.SKPushToken
import dev.baseio.slackdata.protos.SecurePushServiceGrpcKt
import dev.baseio.slackserver.data.models.SKUserPushToken
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.sources.UserPushTokenDataSource
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import dev.baseio.slackserver.services.interceptors.JWT_SIGNING_KEY
import dev.baseio.slackserver.services.interceptors.USER_ID
import dev.baseio.slackserver.services.interceptors.WORKSPACE_ID
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.Dispatchers
import java.security.Key
import java.time.Instant
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class SecurePushService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val pushTokenDataSource: UserPushTokenDataSource,
) : SecurePushServiceGrpcKt.SecurePushServiceCoroutineImplBase(coroutineContext) {

    override suspend fun savePushToken(request: SKPushToken): Empty {
        val authData = AUTH_CONTEXT_KEY.get()
        pushTokenDataSource.savePushToken(request.toSkUserPushToken(authData.userId))
        return empty { }
    }
}

private fun SKPushToken.toSkUserPushToken(userId: String): SKUserPushToken {
    return SKUserPushToken(
        uuid = UUID.randomUUID().toString(),
        userId = userId,
        platform = this.platform,
        token = this.token
    )
}

fun jwtTokenForUser(
    generatedUser: SkUser?,
    key: Key,
    addTime: Long = TimeUnit.DAYS.toMillis(365)
): String? = Jwts.builder()
    .setClaims(
        hashMapOf<String, String?>().apply {
            put(USER_ID, generatedUser?.uuid)
            put(WORKSPACE_ID, generatedUser?.workspaceId)
        }
    )
    .setExpiration(Date.from(Instant.now().plusMillis(addTime))) // valid for 5 days
    .signWith(key)
    .compact()

fun skAuthResult(generatedUser: SkUser?): SKAuthResult {
    val keyBytes =
        Decoders.BASE64.decode(JWT_SIGNING_KEY) // TODO move this to env variables
    val key: Key = Keys.hmacShaKeyFor(keyBytes)
    val jws = jwtTokenForUser(generatedUser, key, TimeUnit.DAYS.toMillis(365))
    return SKAuthResult.newBuilder()
        .setToken(jws)
        .build()
}
