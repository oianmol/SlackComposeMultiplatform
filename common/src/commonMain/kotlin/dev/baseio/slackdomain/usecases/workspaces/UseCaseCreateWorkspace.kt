package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKCreateWorkspaceRequest
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.auth.toSKUser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import SKKeyValueData
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import kotlinx.coroutines.withContext

class UseCaseCreateWorkspace(private val grpcCalls: GrpcCalls,
                             private val SKAuthNetworkDataSource: SKAuthNetworkDataSource,
                             private val skKeyValueData: SKKeyValueData,
                             private val coroutineDispatcherProvider: CoroutineDispatcherProvider) {
    suspend operator fun invoke(email: String, password: String, domain: String) {
        withContext(coroutineDispatcherProvider.io){
            val result = grpcCalls.saveWorkspace(kmskCreateWorkspaceRequest(email, password, domain))
            skKeyValueData.save(AUTH_TOKEN, result.token)
            val user = SKAuthNetworkDataSource.getLoggedInUser().getOrThrow().toSKUser()
            val json = Json.encodeToString(user)
            skKeyValueData.save(LOGGED_IN_USER, json)
        }
    }

    private fun kmskCreateWorkspaceRequest(
        email: String,
        password: String,
        domain: String
    ) = kmSKCreateWorkspaceRequest {
        this.user = kmSKAuthUser {
            this.email = email
            this.password = password
            this.user = kmSKUser {
                this.email = email
            }
        }
        this.workspace = kmSKWorkspace {
            this.name = domain
        }
    }
}