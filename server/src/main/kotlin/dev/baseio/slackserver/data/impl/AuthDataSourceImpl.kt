package dev.baseio.slackserver.data.impl

import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.sources.AuthDataSource
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.findOne
import java.util.*

class AuthDataSourceImpl(private val slackCloneDB: CoroutineDatabase) : AuthDataSource {
    override suspend fun register(user: SkUser): SkUser? {
        // save the user details
        if (user.email.trim().isEmpty()) {
            throw Exception("email cannot be empty!")
        }
        if (user.uuid.trim().isEmpty()) {
            throw Exception("user uuid cannot be empty!")
        }
        slackCloneDB.getCollection<SkUser>().collection.insertOne(
            user
        ).awaitFirstOrNull()
        // save the auth

        return slackCloneDB.getCollection<SkUser>().collection.findOne(SkUser::uuid eq user.uuid).awaitFirstOrNull()
    }
}
