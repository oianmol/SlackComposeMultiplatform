package dev.baseio.slackserver.services.interceptors

import dev.baseio.slackserver.Constants
import io.grpc.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts


class AuthInterceptor : ServerInterceptor {
  private val parser: JwtParser? = Jwts.parserBuilder()
    .setSigningKey(Constants.JWT_SIGNING_KEY)
    .build()

  override fun <ReqT, RespT> interceptCall(
    serverCall: ServerCall<ReqT, RespT>?,
    metadata: Metadata?,
    serverCallHandler: ServerCallHandler<ReqT, RespT>?
  ): ServerCall.Listener<ReqT> {
    val value: String? = metadata?.get(Constants.AUTHORIZATION_METADATA_KEY)

    val status: Status = when {
      value == null -> {
        Status.UNAUTHENTICATED.withDescription("Authorization token is missing")
      }

      !value.startsWith(Constants.BEARER_TYPE) -> {
        Status.UNAUTHENTICATED.withDescription("Unknown authorization type")
      }

      else -> {
        try {
          val token: String = value.substring(Constants.BEARER_TYPE.length).trim { it <= ' ' }
          val claims: Jws<Claims> = parser!!.parseClaimsJws(token)
          val ctx: Context = Context.current().withValue(Constants.CLIENT_ID_CONTEXT_KEY, claims.getBody().getSubject())
          return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler)
        } catch (e: Exception) {
          Status.UNAUTHENTICATED.withDescription(e.message).withCause(e)
        }
      }
    }
    serverCall?.close(status, metadata)
    return object : ServerCall.Listener<ReqT>() {
      // noop
    }
  }
}