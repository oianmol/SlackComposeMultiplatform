package dev.baseio.slackdomain.datasources.local

interface SKLocalDatabaseSource {
    suspend fun clear()
}
