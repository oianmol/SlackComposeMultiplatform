package dev.baseio.slackserver.data.impl

import dev.baseio.slackserver.data.models.SKUserPushToken
import dev.baseio.slackserver.data.sources.UserPushTokenDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class UserPushTokenDataSourceImpl(private val coroutineDatabase: CoroutineDatabase) : UserPushTokenDataSource {
    override suspend fun getPushTokensFor(userIds: List<String>): List<SKUserPushToken> {
        return coroutineDatabase.getCollection<SKUserPushToken>().find(SKUserPushToken::userId `in` userIds).toList()
    }

    override suspend fun savePushToken(toSkUserPushToken: SKUserPushToken) {
        val exists =
            coroutineDatabase.getCollection<SKUserPushToken>()
                .find(SKUserPushToken::token eq toSkUserPushToken.token).toList().isNotEmpty()
        if(!exists){
            coroutineDatabase.getCollection<SKUserPushToken>().insertOne(toSkUserPushToken)
        }
    }
}