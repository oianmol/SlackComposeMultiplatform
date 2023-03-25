package dev.baseio.slackserver.data.impl

import dev.baseio.slackserver.data.sources.AuthDataSource
import dev.baseio.slackserver.data.models.SkAuthUser
import dev.baseio.slackserver.data.models.SkUser
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.findOne
import java.util.*

class AuthDataSourceImpl(private val slackCloneDB: CoroutineDatabase) : AuthDataSource {
    override suspend fun findUser(email: String, workspaceId: String): SkUser? {
        val user = slackCloneDB.getCollection<SkUser>().collection
            .findOne(
                SkUser::email eq email,
                SkUser::workspaceId eq workspaceId
            )
        user.awaitFirstOrNull()?.let { user ->
            slackCloneDB.getCollection<SkAuthUser>().collection
                .findOne(SkAuthUser::userId eq user.uuid)
                .awaitFirstOrNull()
        }
        return null
    }

    override suspend fun register(email: String, user: SkUser): SkUser? {
        //save the user details
        if (email.trim().isEmpty()) {
            throw Exception("email cannot be empty!")
        }
        if (user.uuid.trim().isEmpty()) {
            throw Exception("user uuid cannot be empty!")
        }
        slackCloneDB.getCollection<SkUser>().collection.insertOne(
            user
        ).awaitFirstOrNull()
        // save the auth

        slackCloneDB.getCollection<SkAuthUser>().collection.insertOne(
            SkAuthUser(UUID.randomUUID().toString(), user.uuid)
        ).awaitFirstOrNull()

        return slackCloneDB.getCollection<SkUser>().collection.findOne(SkUser::uuid eq user.uuid).awaitFirstOrNull()
    }
}