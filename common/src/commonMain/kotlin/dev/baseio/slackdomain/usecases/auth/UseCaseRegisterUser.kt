package dev.baseio.slackdomain.usecases.auth

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import SKKeyValueData
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UseCaseRegisterUser(
    private val grpcCalls: GrpcCalls,
    private val SKAuthNetworkDataSource: SKAuthNetworkDataSource,
    private val skKeyValueData: SKKeyValueData
) {
    suspend operator fun invoke(email: String, password: String, workspaceId: String) {
        val result = grpcCalls.register(kmSKAuthUser {
            this.email = email
            this.password = password
            this.user = kmSKUser {
                this.workspaceId = workspaceId
            }
        })

        skKeyValueData.save(AUTH_TOKEN, result.token)
        val user = SKAuthNetworkDataSource.getLoggedInUser().getOrThrow().toSKUser()
        val json = Json.encodeToString(user)
        skKeyValueData.save(LOGGED_IN_USER, json)

    }
}