package dev.baseio.slackserver.data.impl

import at.favre.lib.crypto.bcrypt.BCrypt
import database.SkUser
import dev.baseio.SlackCloneDB
import dev.baseio.slackserver.data.AuthDataSource
import java.util.*

class AuthDataSourceImpl(private val slackCloneDB: SlackCloneDB) : AuthDataSource {
  override fun login(email: String, password: String, workspaceId: String): SkUser? {
    slackCloneDB.slackschemaQueries
      .getUserWithEmailAndWorkspaceId(email, workspaceId)
      .executeAsOneOrNull()?.let { user ->
        val auth = slackCloneDB.slackschemaQueries.getAuth(user.uuid).executeAsOneOrNull()
        auth?.let {
          val result: BCrypt.Result = BCrypt.verifyer().verify(password.toCharArray(), it.password)
          if (result.verified) {
            return user
          }
        }
      }
    return null
  }

  override fun register(email: String, password: String, user: SkUser): SkUser? {
    //save the user details
    slackCloneDB
      .slackschemaQueries
      .insertUser(
        user.uuid,
        user.workspaceId,
        user.gender,
        user.name,
        user.location,
        user.email,
        user.username,
        user.userSince,
        user.phone,
        user.avatarUrl
      )
    // save the auth
    val bcryptHashString: String = BCrypt.withDefaults().hashToString(12, password.toCharArray())

    slackCloneDB.slackschemaQueries.insertAuth(
      UUID.randomUUID().toString(),
      user.uuid,
      bcryptHashString
    )
    return slackCloneDB.slackschemaQueries.getUser(user.uuid).executeAsOneOrNull()
  }
}