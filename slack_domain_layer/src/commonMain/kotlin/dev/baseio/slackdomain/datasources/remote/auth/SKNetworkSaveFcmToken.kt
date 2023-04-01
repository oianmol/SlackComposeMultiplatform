package dev.baseio.slackdomain.datasources.remote.auth

interface SKNetworkSaveFcmToken {
    suspend fun save(token: String)
}
