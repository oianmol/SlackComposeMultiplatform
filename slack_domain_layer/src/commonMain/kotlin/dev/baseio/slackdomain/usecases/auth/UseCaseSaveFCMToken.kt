package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.remote.auth.SKNetworkSaveFcmToken

const val FCM_TOKEN = "fcmToken"

class UseCaseSaveFCMToken(
    private val skLocalKeyValueSource: SKLocalKeyValueSource,
    private val skNetworkSaveFcmToken: SKNetworkSaveFcmToken
) {
    suspend operator fun invoke(token: String) {
        if (skLocalKeyValueSource.get(FCM_TOKEN) != token) {
            skNetworkSaveFcmToken.save(token)
            skLocalKeyValueSource.save(FCM_TOKEN, token)
        }
    }
}