package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SkUser

interface AuthDataSource {
    suspend fun register(user: SkUser): SkUser?
}
