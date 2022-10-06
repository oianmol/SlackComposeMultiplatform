package dev.baseio.slackserver.services.interceptors

import io.grpc.*
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts


const val JWT_SIGNING_KEY = "L8hHXsaQOUjk5rg7XPGv4eL36anlCrkMz8CJ0i/8E/0="
const val USER_ID = "USER_ID"
const val WORKSPACE_ID = "WORKSPACE_ID"
const val BEARER_TYPE = "Bearer"
val AUTHORIZATION_METADATA_KEY: Metadata.Key<String> = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)
val AUTH_CONTEXT_KEY: Context.Key<AuthData> = Context.key("credentials")

data class AuthData(val userId: String, val workspaceId: String)

class AuthInterceptor : ServerInterceptor {
  private val parser: JwtParser = Jwts.parserBuilder()
    .setSigningKey(JWT_SIGNING_KEY)
    .build()

  override fun <ReqT, RespT> interceptCall(
    serverCall: ServerCall<ReqT, RespT>?,
    metadata: Metadata?,
    serverCallHandler: ServerCallHandler<ReqT, RespT>?
  ): ServerCall.Listener<ReqT> {
    val value: String? = metadata?.get(AUTHORIZATION_METADATA_KEY)
    val status: Status = when {
      value == null -> {
        val ctx: Context = Context.current().withValue(AUTH_CONTEXT_KEY, AuthData("",""))
        return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler)
        //Status.UNAUTHENTICATED.withDescription("Authorization token is missing")
      }

      !value.startsWith(BEARER_TYPE) -> {
        Status.UNAUTHENTICATED.withDescription("Unknown authorization type")
      }

      else -> {
        try {
          val token: String = value.substring(BEARER_TYPE.length).trim { it <= ' ' }
          val claims: Jws<Claims> = parser.parseClaimsJws(token)
          val authData = AuthData(
            userId = claims.body[USER_ID].toString(),
            workspaceId = claims.body[WORKSPACE_ID].toString()
          )
          val ctx: Context = Context.current().withValue(AUTH_CONTEXT_KEY, authData)
          return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler)
        } catch (e: Exception) {
          e.printStackTrace()
          Status.UNAUTHENTICATED.withDescription(e.message).withCause(e)
        }
      }
    }
    serverCall?.close(status, metadata)
    return serverCallNoOp()
  }

  private fun <ReqT> serverCallNoOp(): ServerCall.Listener<ReqT> {
    return object : ServerCall.Listener<ReqT>() {
      // noop
    }
  }
}